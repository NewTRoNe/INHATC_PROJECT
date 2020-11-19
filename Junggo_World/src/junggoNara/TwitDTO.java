package junggoNara;

public class TwitDTO {
	 private int post_id;
	  private String id;
	  private String twit_content;
	  private String twit_time;
	  
	public int getPost_id() {
		return post_id;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTwit_content() {
		return twit_content;
	}
	public void setTwit_content(String twit_content) {
		this.twit_content = twit_content;
	}
	public String getTwit_time() {
		return twit_time;
	}
	public void setTwit_time(String twit_time) {
		this.twit_time = twit_time;
	}
}
