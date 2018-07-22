package com.vijayanix.iot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hxhoua on 2018/7/20.
 */

@RunWith(JUnit4.class)
public class RegixTest {
	@Test
	public void aa(){
		String txt="device/device/35838489/65535";

		String txt2="device/device/35838489";


		String re1="(device)";	// Variable Name 1
		String re2="(\\/)";	// Any Single Character 1
		String re3="(device)";	// Word 1
		String re4="(\\/)";	// Any Single Character 2
		String re5="(\\d+)";	// Integer Number 1
		String re7="(\\/*)";	// Any Single Character 3
		String re8="(\\d*)";	// Integer Number 2

		String regix = re1+re2+re3+re4+re5+re7+re8;
		System.out.println(regix);
		Pattern p = Pattern.compile(regix,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
		Matcher m = p.matcher(txt2);
		if (m.find())
		{
			String var1=m.group(1);
			String c1=m.group(2);
			String word1=m.group(3);
			String c2=m.group(4);
			String int1=m.group(5);
			String d1=m.group(6);
			String c3=m.group(7);
//			String int2=m.group(8);
			System.out.println(int1);
			System.out.print("("+var1.toString()+")"+"("+c1.toString()+")"+"("+word1.toString()+")"+"("+c2.toString()+")"+"("+int1.toString()+")"+"("+d1.toString()+")"+"("+c3.toString()+")"+"\n");
		}
	}

}
