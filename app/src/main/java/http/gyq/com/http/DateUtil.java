package http.gyq.com.http;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	private static final String tag = "DateUtil";

	/**
	 * Unix时间戳转换成时间格式
	 * @param dataFormat
	 * @param timeStamp
	 * @return
	 */
	public static String formatData(String dataFormat, long timeStamp) {
		String result = "";
//		if (timeStamp == 0) {
//			return result;
//		}
		timeStamp = timeStamp * 1000;
		SimpleDateFormat format = new SimpleDateFormat(dataFormat);
		result = format.format(new Date(timeStamp));
		return result;
	}

	public static Date add(String dateStr, int addDay) {
		try {
			// ת���ַ�ΪCalendar
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			// System.out.println("测试 ..啊"+date);
			calendar.add(Calendar.DATE, addDay);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(tag, "", e);
		}
		return null;
	}

	public static Date add(Date date, int addDay) {
		try {
			// ת���ַ�ΪCalendar
			if (date == null)
				return null;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			calendar.add(Calendar.DATE, addDay);
			return calendar.getTime();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(tag, "", e);
		}
		return null;
	}

	public static String getDateStr(String dateTime, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(dateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			Log.e(tag, "", e);
			return null;
		}
		return sdf.format(date);
	}

	public static String getDay(String dateTime, String format) {
		SimpleDateFormat sdf1 = new SimpleDateFormat(format);
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd");
		Date date = null;
		try {
			date = sdf1.parse(dateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sdf2.format(date);
	}

	public static Date getDate(String dateTime, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(dateTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public static String getDate(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date.getTime());
	}

	public static String getDate1(Date date) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(date.getTime());
	}

	public static Date getToday() {
		return new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
	}

	public static String getDateFormat(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date.getTime());
	}

	public static int compare(Date date1, Date date2, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr1 = sdf.format(date1);
		String dateStr2 = sdf.format(date2);
		return dateStr1.compareTo(dateStr2);
	}

	/**
	 * 
	 * @param date1
	 *            比较日期
	 * @param date2
	 *            上传日期
	 * @return 0:相等，1大于 -1 小于
	 */
	public static int compareTo(String date1, String date2) {
		String[] str1 = date1.split("-");
		String[] str2 = date2.split("-");
		if (str1.length != 3 || str2.length != 3) {// 格式不对，返回 1
			return 1;
		}
		if (Integer.parseInt(str1[0]) > Integer.parseInt(str2[0])) {// 比较年
			return 1;
		} else if (Integer.parseInt(str1[0]) < Integer.parseInt(str2[0])) {
			return -1;
		} else {// 比较月
			if (Integer.parseInt(str1[1]) > Integer.parseInt(str2[1])) {
				return 1;
			} else if (Integer.parseInt(str1[1]) < Integer.parseInt(str2[1])) {
				return -1;
			} else {// 比较日
				if (Integer.parseInt(str1[2]) > Integer.parseInt(str2[2])) {
					return 1;
				} else if (Integer.parseInt(str1[2]) < Integer
						.parseInt(str2[2])) {
					return -1;
				} else {
					return 0;
				}
			}
		}
	}

	public static String TalkCompareTo(String data) {
		if (data == null || "".equals(data)) {
			return "";
		}
		long datal = Long.parseLong(data);
		Date newd = new Date();
		long newdate = newd.getTime() / 1000;
		long oneminute = 60L;// 分钟
		long onehours = 60L * 60;// 小时
		long oneday = 60L * 60 * 24;// 天
		long onemonth = 60L * 60 * 24 * 30;// 月
		long oneyear = 60L * 60 * 24 * 30 * 12;// 年
		long result = newdate - datal;
		String str = "";
		if (result < oneminute) {// 小于1分钟
			str = "刚刚";
		} else if (oneminute <= result && result < onehours) {// 分钟
			str = ((int) result / oneminute) + "分钟前";
		} else if (onehours <= result && result < oneday) {// 小时
			str = ((int) result / onehours) + "小时前";
		} else if (oneday <= result && result < onemonth) {// 天
			str = ((int) result / oneday) + "天前";
		} else if (onemonth <= result && result < oneyear) {// 月
			str = ((int) result / onemonth) + "月前";
		} else if (oneyear <= result) {// 年
			str = ((int) result / oneyear) + "年前";
		}
		/*
		 * if(result<=60L){//一分钟前 str = "一分钟"; }else
		 * if(result>60L&&result<=60L*30L){//半个小时前 str = "30分钟"; }else
		 * if(result>60L*30L&&result<=60L*60L){//一个小时前 str = "1小时前"; }else
		 * if(result>60L*60L&&result<=60L*180L){//三个小时前 str = "3小时前"; }else
		 * if(result>60L*180L&&result<=60L*1200L){//20个小时前 str = "20小时前"; }else
		 * if(result>60L*1200L&&result<=60L*1380L){//23个小时前 str = "23小时前"; }else
		 * if(result>60L*1380L&&result<=60L*1440L){//一天前 str = "一天前"; }else
		 * if(result>60L*1440L&&result<=60L*1440L*30L){//30天前 str = "30天前";
		 * }else if(result>60L*1440L*30L&&result<=60L*1440L*30L*12L){//一个月前 str
		 * = "1个月前"; }else if(result>60L*1440L*30L*12){//一年前 str = "1年前"; }
		 */
		return str;
	}

	/**
	 * 2017-1-1 maoshenbo
	 * @param year
	 * @param month
	 * @param day
     * @return
     */
	public static String FormatyyyyMMdd(int year, int month, int day){
		String newTime = null;
		if(year >= 0 && month>=0 && day>=0){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date curDate = new Date(year-1900, month-1, day);//获取当前时间
			newTime = formatter.format(curDate);
		}
		return newTime;
	}
	public static String getDateStringSimple(int year, int month, int day){
		return String.format("%d%02d%02d",year,month,day);
	}

	/**
	 * 2017-1-1 maoshenbo
	 * @param time
	 * @return
     */
	public static String FormatyyyyMMdd(long time){
		String newTime = null;
		if(time >= 0){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date curDate = new Date(time);//获取当前时间
			newTime = formatter.format(curDate);
		}
		return newTime;
	}

	/**
	 * 2017-1-1 maoshenbo
	 * @return
     */
	public static String FormatyyyyMMdd(){
		String newTime = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date curDate = new Date();//获取当前时间
		newTime = formatter.format(curDate);
		return newTime;
	}

	/**
	 * 获取天
	 * @return
     */
	public static String FormatyyMMdd(){
		String newTime = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
		Date curDate = new Date();//获取当前时间
		newTime = formatter.format(curDate);
		return newTime;
	}
	public static String FormatyyMMdd(long time){
		String newTime = null;
		if(time >= 0){
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
			Date curDate = new Date(time);//获取当前时间
			newTime = formatter.format(curDate);
		}
		return newTime;
	}

	/**
	 * 获取小时
	 * @return
     */
	public static String FormatyyMMddHH(){
		String newTime = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHH");
		Date curDate = new Date();//获取当前时间
		newTime = formatter.format(curDate);
		return newTime;
	}
	public static String FormatyyMMddHH(long time){
		String newTime = null;
		if(time >= 0){
			SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHH");
			Date curDate = new Date(time);//获取当前时间
			newTime = formatter.format(curDate);
		}
		return newTime;
	}

	/**
	 * 获取全时间
	 * @return
     */
	public static String FormatyyMMddHHmmss(){
		String newTime = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
		Date curDate = new Date();//获取当前时间
		newTime = formatter.format(curDate);
		return newTime;
	}

	/**
	 * 日志时间
	 * @param time
	 * @return
     */
	public static String FormatMMddHHmmss(long time){
		String newTime = null;
		if(time >= 0){
			SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss");
			Date curDate = new Date(time);//获取当前时间
			newTime = formatter.format(curDate);
		}
		return newTime;
	}

	/**
	 * 转化为时间戳(到秒)
	 * @param time
	 * @return
     */
	public static long getLongData(String time){
		long newTime = 0;
		if(time!=null){
			SimpleDateFormat format =  new SimpleDateFormat("yyyyMMdd HH:mm:ss");
			try {
				Date date = format.parse(time);
				String thisTime = String.valueOf(date.getTime());
				newTime = Long.parseLong(thisTime.substring(0, thisTime.length()-3));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return newTime;
	}

	/**
	 * 获取时间6:58格式
	 * @param time
	 * @return
     */
	public static String FormatHHmm(long time){
		String newTime = null;
		if(time>=0){
			SimpleDateFormat format =  new SimpleDateFormat("HH:mm");
			Date curDate = new Date(Long.parseLong(String.valueOf(time)+"000"));//获取当前时间
			newTime = format.format(curDate);
		}
		return newTime;
	}

	/**
	 * 获取日月，用于在pager中显示
	 * @param date
	 * @return
     */
	public static String getMonthDay(String date){
		String newDate = null;
		if(date!=null&&date.length()==8){
			String month = date.substring(4, 6);
			if(Integer.parseInt(month)>=10){
				month = month + "月";
			}else {
				month = month.substring(1) + "月";
			}
			String day = date.substring(6, 8);
			if(Integer.parseInt(day)>=10){
				day = day + "日";
			} else {
				day = day.substring(1) + "日";
			}
			newDate = month + day;
		}
		return newDate;
	}
	//获取系统当前时间戳dcc
	public static String getCurrentTime(){
		long time= System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳

		String str= String.valueOf(time);

		return str;
	}
}
