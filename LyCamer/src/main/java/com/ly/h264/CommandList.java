package com.ly.h264;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 消息列表类
 * </p>
 * 
 * @author Jack Zhou
 * @version $Id: CommandList.java,v 0.1 2012-3-20 上午11:04:24 Jack Exp $
 */
@SuppressWarnings("unchecked")
public class CommandList {
	private List cmdList = new ArrayList();

	public CommandMessage getCommand() {

		synchronized (this) {
			if (cmdList.isEmpty()) {
				return null;
			}

			return (CommandMessage) (cmdList.remove(0));
		}
	}

	public void addCommand(CommandMessage cmdMsg) {
		synchronized (this) {
			cmdList.add(cmdMsg);
		}
	}

	public void ClearCommand() {
		synchronized (this) {
			cmdList.clear();
		}
	}
}
