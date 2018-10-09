package com.hywy.luanhzt.exceptions;

public class BackGroundLoginException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public BackGroundLoginException()
	{
		super("login in background failure!");
	}
	
}
