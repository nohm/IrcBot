package org.snack.irc.model;

public class FThread {
	public final String name;
	public final String subject;
	public final String post;
	public final String url;

	public FThread(String name, String subject, String post, String url) {
		this.name = name;
		this.subject = subject;
		this.post = post;
		this.url = url;
	}
}
