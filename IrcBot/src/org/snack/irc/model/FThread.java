package org.snack.irc.model;

public class FThread {
	public final String name;
	public final String subject;
	public final String post;
	public final String url;
	public final String comment;
	public final String replies;

	public FThread(String name, String subject, String post, String url, String comment, String replies) {
		this.name = name;
		this.subject = subject;
		this.post = post;
		this.url = url;
		this.comment = comment;
		this.replies = replies;
	}
}
