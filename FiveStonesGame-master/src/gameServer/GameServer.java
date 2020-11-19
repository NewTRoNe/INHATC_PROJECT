package gameServer;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;

import protocolData.*;

public class GameServer extends Thread {
	private boolean flag = true;
	private Protocol data;
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	protected String name;
	private ServerInterface server;
	private LobbyInterface lobby;
	private GameRoomInterface room;
	private int userLocation, roomNumber;

	private boolean isReady = false;
	private boolean isLogin = false;
	private boolean isMyTurn = false;

	public GameServer(Socket socket, LobbyInterface lobby) {
		System.out.println("Game Server New!!");
		LogFrame.print("Game Server New!!");

		setUserLocation(ServerInterface.LOBBY);

		this.lobby = lobby;
		this.name  = socket.getInetAddress().toString();
		setLobbyInstance();

		this.socket = socket;
		try {
			in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("-- ["+name+"]");
			lobby.subSocket(name);

            if( this.socket != null) {
	            try {
	                this.socket.close();
	            } catch (IOException e1) {
	                e1.printStackTrace();
	            }
            }
			return;
        }

		this.start();
	}

	public void run() {
		while (flag) {

			try {
				data = (Protocol) in.readObject();

				System.out.println("=====================================");
				LogFrame.print("=====================================");
				
				System.out.println("From Clinet : " + data);
				LogFrame.print("From Clinet : " + data);
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();

			} catch (EOFException e) {
				continue;

			} catch ( SocketException e ) {
				System.out.println("E.GameServer01");
				e.printStackTrace();
				flag = false;
				
				exitUser();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				break;

			} 

			if (data instanceof ChatData)
				analysisChatData((ChatData) data);

			else if (data instanceof LobbyData)
				analysisLobbyData((LobbyData) data);

			else if (data instanceof GameLobbyData)
				analysisGameLobbyData((GameLobbyData) data);

			else if (data instanceof GameData)
				analysisGameData((GameData) data);

			else {
			}
		}

		System.out.println("Exit");

		lobby.printState();

	}

	protected void sendMessage(Protocol data) throws Exception {
		try {
			out.writeObject(data);
		} catch ( NullPointerException e ) {
			System.out.println("E.GameServer02");
			throw new Exception("E.GameServer03. NullPointerException!! ");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("E.GameServer04");
			throw new Exception("E.GameServer05" + e );
		}
	}

	protected void stopThisThread() {
		this.flag = false;
	}

	/*
	 * when First enter Game or exit GameRoom call!
	 */
	private void setRoomUserList() {
		sendUserList();
		sendRoomList();
	}

	private void analysisChatData(ChatData data) {

		switch (data.getProtocol()) {

		case ChatData.ENTER:
			/*
			 * Enter program
			 */
			// Version Check!!
			String version = data.getMessage(); // get Client Version..
			this.name = data.getName();
			
			if (!version.equals(""+MainServer.VERSION)) {
				analysisChatData(new ChatData(data.getName(), null, ChatData.LOGIN_CHECK));
			
			} else {
				setLogin(true);
				server.broadcasting(data);
				setRoomUserList();
			}
			
			break;

		case ChatData.LOGIN_CHECK:
			try {
				sendMessage(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

		case ChatData.EXIT:
		
			exitUser( this.data );
			break;

		case ChatData.MESSAGE:
			/*
			 * send message
			 */
			server.broadcasting(data);
			break;

		case ChatData.MESSAGE_SLIP:
			/*
			 * send Slip message
			 */
			server.sendSlip(data);
			break;

		case ChatData.SEND_TOTAL_USER:
			// client request userlist.
			Vector<String> list = new Vector<String>();

			for (GameServer temp : server.getUserList().getCollection())
				list.add(temp.getUserName());

			data.setUserList(list);
			try {
				sendMessage(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		default:
			break;
		}

	}

	private void exitUser() {
		ChatData data = new ChatData(name, "exit", ChatData.EXIT );
		exitUser( data );
	}
	
	private void exitUser( Protocol data ) {
		if(isLogin) {
			server.broadcasting(data);
		}
			server.subSocket(data.getName());
			sendUserList();

		if (getUserLocation() != ServerInterface.LOBBY)
			analysisGameLobbyData(new GameLobbyData(data.getName(), null,
					GameLobbyData.EXIT_ROOM));

		stopThisThread();
	}
	
	
	/*
	 * When client is First Connetion...
	 */
	protected void sendUserList() {
		System.out.println("In Method[sendUserList()]");
		LogFrame.print("In Method[sendUserList()]");
		
		ChatData data = new ChatData(null, null, ChatData.SEND_USER_LIST);
		data.setUserList(server.getStringUser());
		server.broadcasting(data);
	}

	protected void sendRoomList() {
		server.broadcasting(new LobbyData(lobby.getRoomListAsString(),
				LobbyData.SEND_ROOMLIST));
	}

	private void analysisLobbyData(LobbyData data) {
		switch (data.getProtocol()) {

		case LobbyData.CREATE_ROOM:
			/*
			 * create Room...
			 */
			setUserLocation(ServerInterface.IN_GAME_ROOMKING);

			this.room = new GameRoom(data.getRoomName(), lobby.getUserList());
			lobby.addRoom(room);
			roomNumber = room.getNumber();
			setReady(true); // roomKing is always ready...

			setRoomUserList();

			setRoomInstance(room);

			data.setRoomNumber(roomNumber);
			try {
				sendMessage(data);
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		case LobbyData.ENTER_TO_ROOM:
			/*
			 * enter to room....
			 */
			room = lobby.getSelectedRoom(data.getRoomName());
			int userCount = room.getUserCounter();

			if (userCount < 2) {
				setUserLocation(ServerInterface.IN_GAME_CRHARANGER);

				sendUserList();

				roomNumber = room.getNumber();
				setRoomInstance(room);

				data.setUserList(room.getStringUser());
				data.setRoomNumber(roomNumber);

				try {
					sendMessage(data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				room.sendTo(ServerInterface.IN_GAME_ROOMKING, data);
				sendUserList();

			} else {
				room = null;

				try {
					sendMessage(new ChatData("Error", "방 생성 오류!.", ChatData.MESSAGE));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;

		case LobbyData.SEND_ROOMLIST:
			/*
			 * send Room LIst...
			 */
			break;

		case LobbyData.EXIT_GAME:
			/*
			 * exit Game;;;
			 */
			break;

		default:
			break;
		}

	}

	private void setRoomInstance(GameRoomInterface room) {
		this.room = room;
		this.server = room;
	}

	private void setLobbyInstance() {
		this.room = null;
		this.server = lobby;
	}

	private void analysisGameLobbyData(GameLobbyData data) {
		switch (data.getProtocol()) {

		case GameLobbyData.CANCEL_READY:
			/*
			 * ready is canceled...
			 */
			setReady(false);
			room.sendTo(ServerInterface.IN_GAME_ROOMKING, data);

			break;

		case GameLobbyData.EXIT_ROOM:
			/*
			 * exit the room...
			 */

			if (userLocation == ServerInterface.IN_GAME_ROOMKING) {
				lobby.subRoom(roomNumber);
				setUserLocation(ServerInterface.LOBBY);
				server.broadcasting(data);
				room.exitRoomMaster(data);

			} else {
				setUserLocation(ServerInterface.LOBBY);
			}

			setLobbyInstance();

			sendUserList();
			setRoomUserList();

			break;

		case GameLobbyData.GAME_READY:
			/*
			 * game is ready...
			 */
			setReady(true);

			if (room.isAllReady())
				room.sendTo(ServerInterface.IN_GAME_ROOMKING, data);

			break;

		case GameLobbyData.GAME_START:
			/*
			 * game start!!
			 */

			setMyTurn(true);

			data.setMessage("Game Start!");
			data.setName("NOTICE");

			room.broadcasting(data);
			sendUserList();
			room.setStart(true);

			break;

		default:
			break;
		}
	}

	private void analysisGameData(GameData data) {
		switch (data.getProtocol()) {
		case GameData.EXIT_THEGAME:
			/*
			 * exit the game
			 */
			if (this.getUserLocation() == ServerInterface.IN_GAME_ROOMKING) {
				room.newGame();

				server.broadcasting(new GameData(name, true,
						GameData.EXIT_THEGAME));

				sendUserList();

			} else {
				server.broadcasting(new GameData(name, false,
						GameData.EXIT_THEGAME));
				sendUserList();
			}
			setUserLocation(ServerInterface.LOBBY);

			break;

		case GameData.SEND_STONE_LOCATION:
			/*
			 * send stone location..
			 */
			if (!room.isStart())
				room.setStart(true);

			if (isMyTurn()) {
				// Select Stone Color.
				if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING)
					data.setBlack(true);
				else
					data.setBlack(false);

				boolean isWin = room.addStone(data.getStoneLocation(), data
						.isBlack());

				moveTurn();

				server.broadcasting(data);

				// when anyone win the game, this send victory message.
				if (isWin) {
					sendVictory(data.isBlack());
					room.setStart(false);
				}
			} else { // not my turn.
				// send Warring!!
				System.out.println("============= no TURN!!!  =============");
				LogFrame.print("============= no TURN!!!  =============");
				if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING)
					room.sendTo(ServerInterface.IN_GAME_ROOMKING, new ChatData(
							"Error", "내 턴이 아닙니다!", ChatData.MESSAGE));
				else
					room.sendTo(ServerInterface.IN_GAME_CRHARANGER, new ChatData(
							"Error", "내 턴이 아닙니다!.", ChatData.MESSAGE));
			}

			break;

		case GameData.SEND_RESULT:
			/*
			 * After Victory, then restart!! install(?)...
			 */
			room.newGame();

			break;

		case GameData.REQUEST_RETURN:
			if(isMyTurn()) {
				if (room.isStart()) {

					if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING) {
						room.sendTo(ServerInterface.IN_GAME_CRHARANGER, data);
						room.sendTo(ServerInterface.IN_GAME_ROOMKING, new ChatData(
							"E.GameServer12", "E.GameServer13", ChatData.MESSAGE));


					} else {
						room.sendTo(ServerInterface.IN_GAME_ROOMKING, data);
						room.sendTo(ServerInterface.IN_GAME_CRHARANGER,
								new ChatData("E.GameServer13", "E.GameServer14",
										ChatData.MESSAGE));

					}
				}
			} else {
				System.out.println("E.GameServer15");
				if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING)
					room.sendTo(ServerInterface.IN_GAME_ROOMKING, new ChatData(
						"E.GameServer16", "E.GameServer17", ChatData.MESSAGE));
				else
					room.sendTo(ServerInterface.IN_GAME_CRHARANGER, new ChatData(
						"E.GameServer18", "E.GameServer19", ChatData.MESSAGE));
			}

			break;

		case GameData.RESPONSE_RETURN:
			if (data.isReturn()) {
				int flag = isMyTurn() ? 1 : 2;
				room.subLastStone(flag);

				if (flag == 2)
					data = new GameData(name, true, GameData.RESPONSE_RETURN);
				else
					moveTurn();
				
				room.broadcasting(data);

			} else if (getUserLocation() == ServerInterface.IN_GAME_ROOMKING)
				room.sendTo(ServerInterface.IN_GAME_CRHARANGER, new ChatData(
						"E.GameServer20", "E.GameServer21", ChatData.MESSAGE));
			else
				room.sendTo(ServerInterface.IN_GAME_ROOMKING, new ChatData(
						"E.GameServer22", "E.GameServer23", ChatData.MESSAGE));
			;

			break;

		case GameData.SEND_GAME_MESSAGE:

			break;

		default:
			System.out.println("Error!!!!! : switch loop out!!!");
			break;
		}

	}

	private void sendVictory(boolean isBlack) {
		room.broadcasting(new GameData(name, isBlack, GameData.SEND_RESULT));
	}

	private void moveTurn() {
		setMyTurn(!isMyTurn());
		room.moveTurn(userLocation);
	}

	public String toString() {
		return name;
	}

	public int getUserLocation() {
		return userLocation;
	}

	public String getUserName() {
		return name;
	}

	public void setUserLocation(int location) {
		this.userLocation = location;
	}

	public int getRoomNumber() {
		return roomNumber;
	}

	private void setReady(boolean bool) {
		isReady = bool;
	}

	public boolean isReady() {
		return isReady;
	}

	public boolean isMyTurn() {
		return isMyTurn;
	}

	public void toGameServer(Protocol data) {
		analysisGameLobbyData((GameLobbyData) data);
	}

	public void setMyTurn(boolean isMyTurn) {
		this.isMyTurn = isMyTurn;
	}

	public boolean isLogin() {
		return isLogin;
	}
	

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	

}