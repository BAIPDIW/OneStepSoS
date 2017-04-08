package com.cdx.onestepsos;

import java.util.UUID;

/**
 * 蓝牙工具类
 */
public class BluetoothTools {

	/**
	 * 本程序所使用的UUID
	 */
	public static final UUID PRIVATE_UUID = UUID.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");

	/**
	 * 字符串常量，Intent中的数据
	 */
	public static final String DATA = "DATA";

	/**
	 * Action类型标识符，Action类型为读到数据
	 */
	public static final String ACTION_READ_DATA = "ACTION_READ_DATA";

	/**
	 * Action：开启服务器
	 */
	public static final String ACTION_START_SERVER = "ACTION_START_SERVER";

	/**
	 * Action：关闭后台Service
	 */
	public static final String ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE";

	/**
	 * Action：到Service的数据
	 */
	public static final String ACTION_DATA_TO_SERVICE = "ACTION_DATA_TO_SERVICE";

	/**
	 * Action：到游戏业务中的数据
	 */
	public static final String ACTION_DATA_TO_GAME = "ACTION_DATA_TO_GAME";

	/**
	 * Action：连接成功
	 */
	public static final String ACTION_CONNECT_SUCCESS = "ACTION_CONNECT_SUCCESS";

	/**
	 * Action：连接错误
	 */
	public static final String ACTION_CONNECT_ERROR = "ACTION_CONNECT_ERROR";

	/**
	 * Message类型标识符，连接成功
	 */
	public static final int MESSAGE_CONNECT_SUCCESS = 0x00000002;

	/**
	 * Message：连接失败
	 */
	public static final int MESSAGE_CONNECT_ERROR = 0x00000003;

	/**
	 * Message：读取到一个对象
	 */
	public static final int MESSAGE_READ_OBJECT = 0x00000004;
}
