package gui;

import gameClient.ClientInterface;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


@SuppressWarnings("serial")
public class GameLobby extends JPanel implements GameLobbyInter, PanelInterface {

	private UserListFrame userListFrame;
	
	/*
	 * Component of Gamer Panel
	 */
	private JLabel m_roomInfo, m_userInfo1, m_userInfo2;
	
	private JPanel m_gamerPanel = new JPanel() {
		public void paint(Graphics g) {
			this.paintComponents(g);
		}
	};

	private ImageButton m_gamer1Button, m_gamer2Button;

	/*
	 * Component of User List Panel
	 */
	private JPanel m_userListPanel = new JPanel() {
		public void paint(Graphics g) {
			this.paintComponents(g);
		}
	};

	private JList m_userList = new JList();

	/*
	 * Component of Chat Window
	 */
	private JPanel m_logWinPanel = new JPanel() {
		public void paint(Graphics g) {
			this.paintComponents(g);
		}
	};

	private JTextArea m_logWindow = new JTextArea(5, 20);

	private JTextField m_textInput = new JTextField();

	private ImageButton m_sendButton = new ImageButton("image/gameLobbySendButton.png", "SEND", "image/gameLobbySendButtonOver.png");

	private JScrollPane m_scPaneLogWin;
	private JScrollBar m_vScroll;

	/*
	 * Component of Info Panel
	 */
	private JPanel m_infoPanel = new JPanel() {
		public void paint(Graphics g) {
			this.paintComponents(g);
		}
	};

	private ImageButton m_exitButton = new ImageButton("image/gameLobbyExitButton.png", "나가기", "image/gameLobbyExitButtonOver.png");
	
	protected AbstractButton m_startButton;
	
	private Vector<String> vc = new Vector<String>();
	
	
	private ClientInterface client;
	private EventExecute event;

	
	private boolean isRoomKing = true;
	/*
	 *  Constructor
	 */
	public GameLobby(ClientInterface client, boolean isRoomKing) {
		
		this.client = client;
		this.event = new EventExecute(this, this.client);
		this.isRoomKing = isRoomKing;
		
		execute();
		
		System.out.println("E.GameLobby001");
	}
	
	public static void main(String[] args) {
		JFrame frame = new JFrame("Network FIve Eyes Ver. 1.0");
		Container cp = frame.getContentPane();
		cp.add(new GameLobby(null, true));
		
		frame.setSize(340,440);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		System.out.println("Exit");
	}
	
	private void execute() {
		add(generatorGamerPanel());
		add(generatorUserListPanel());
		add(generatorChatWindowPanel());
		add(generatorInfoPanel());
		setLayout(null);
	}
	
	public void paint(Graphics g) {
		ImageIcon icon = new ImageIcon(
				URLGetter.getResource("image/gameLobbyBg.png"));
		g.drawImage(icon.getImage(),0,0,null,null);
		this.paintComponents(g);
	}
	
	private JPanel generatorGamerPanel() {
		m_roomInfo = new JLabel();
		m_userInfo1 = new JLabel();
		m_userInfo2 = new JLabel();
		
		m_gamer1Button = new ImageButton("image/user1.png", "Empty!");
		m_gamer2Button = new ImageButton("image/user2.png", "Please Wait....");
		
		m_gamerPanel.add(m_roomInfo);
		m_roomInfo.setBounds(5,5,200,10);
		
		m_gamerPanel.add(m_userInfo1);
		m_userInfo1.setBounds(50,200,100,10);
		m_gamerPanel.add(m_gamer1Button);
		m_gamer1Button.setBounds(40,50, 100,130);
		
		m_gamerPanel.add(m_userInfo2);
		m_userInfo2.setBounds(210,200,100,30);
		m_gamerPanel.add(m_gamer2Button);
		m_gamer2Button.setBounds(170, 50, 100, 130);
		
		m_gamerPanel.setLayout(null);
		m_gamerPanel.setBounds(5,5,300,245);

		return m_gamerPanel;
	}
	
	private JPanel generatorUserListPanel() {
		m_userList.setListData(vc);

		m_userListPanel.setLayout(null);
		m_userListPanel.setBounds(270, 5, 230, 130);

		return m_userListPanel;
	}

	private JPanel generatorChatWindowPanel() {
		m_scPaneLogWin = new JScrollPane(m_logWindow,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		m_logWindow.setEditable(false);
		m_scPaneLogWin.setBounds(5,5,250,100);
		m_logWindow.setLineWrap(true);
		
		m_vScroll = m_scPaneLogWin.getVerticalScrollBar();
		m_vScroll.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				if(!e.getValueIsAdjusting()) m_vScroll.setValue(m_vScroll.getMaximum());
				
			}
		});
		
		m_logWinPanel.add(m_textInput);
		m_textInput.setBounds(5, 110, 180, 30);
		m_logWinPanel.add(m_sendButton);
		m_sendButton.setBounds(195, 110, 60, 30);
		

		m_logWinPanel.add(m_scPaneLogWin);
		m_logWinPanel.setLayout(null);
		m_logWinPanel.setBounds(5, 250, 260, 200);
		
		m_textInput.addActionListener(event);
		m_sendButton.addActionListener(event);
		
		return m_logWinPanel;
	}

	private JPanel generatorInfoPanel() {
		m_startButton = new ImageButton("image/startButton.png", "START!", "image/startButtonOver.png");
		m_infoPanel.add(m_startButton);
		m_startButton.setBounds(5,50,60,40);
		m_infoPanel.add(m_exitButton);
		m_exitButton.setBounds(5,95,60,40);
		
		m_infoPanel.setLayout(null);
		m_infoPanel.setBounds(260, 250, 230,200);
				
		m_startButton.addActionListener(event);
		m_exitButton.addActionListener(event);
				
		return m_infoPanel;
	}

	public void setClickable(boolean b) {
		m_startButton.setEnabled(b);
	}

	public void setTextToLogWindow(String str) {
		m_logWindow.append(str);
	}

	public void setUserList(Vector<String> userList) {
		Vector<String> temp = new Vector<String>();
		
		setRoomKing(userList.get(0));

		if(userList.size() < 2)
			setCrhallenger("Pleas wait....");
		else
			setCrhallenger(userList.get(1));
		
		int i = 0;
		for (String user : userList)
			if (i++ > 1) temp.add(user);
		
		m_userList.setListData(temp);
	}


	public JList getJList() {
		return m_userList;
	}

	public void unShow() {
		this.setVisible(false);
	}
	
	public String getInputText() {
		return m_textInput.getText();
	}


	public void setTextToInput(String string) {
		m_textInput.setText(string);
	}

	public void setRoomKing(String name) {
		m_userInfo1.setText(name);
		this.repaint();
	}

	public void setCrhallenger(String name) {
		m_userInfo2.setText(name);
		this.repaint();
	}

	public void setGameRoomInf(String info) {
		m_roomInfo.setText(info);
	}

	public void setStartButton(boolean isRoomKing) {
		if (isRoomKing) {
			m_startButton.setText("START");
			
			this.setButtonEnable(true);
			
		} else {
			m_startButton.setText("START");
		}
		
	}

	public void setButtonEnable(boolean clickable) {
		m_startButton.setEnabled(true);
		System.out.println("Clikable******************");
	}

	public void setTotalUser(Vector<String> userList) {
		userListFrame.setUserList(userList);
	}

	public void setUserListFrame(UserListFrame userListFrame) {
		this.userListFrame = userListFrame;
	}
	
	public String getGameInfo() {
		return m_roomInfo.getText() + "|" + m_userInfo1.getText() + "|" + m_userInfo2.getText();
	}

	public void setPanel(PanelInterface panel) {
		
	}

	public int[] getFrameSize() {
		int size[] = {340,440};
		return size;
	}

	public void setPanel(GameLobbyInter panel) {
	}
	public void setPanel(LobbyGuiInter panel) {
	}
	public void setPanel(RoomGuiInter panel) {
	}
}