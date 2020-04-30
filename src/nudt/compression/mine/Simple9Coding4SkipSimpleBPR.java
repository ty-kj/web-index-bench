package nudt.compression.mine;

import nudt.structures.BufferWrapper;
import nudt.utils.BitUtils;

/**
 * buffer position reset if decode partial number of values (<256)
 * @author <a href="mailto: jk_365@126.com"> jk_365</a>
 */

public class Simple9Coding4SkipSimpleBPR {// extends IntCompressor{
    private static final int[] limits = {
    		0xfffffff, // 1 28 bit
            0x3fff,    // 2 14 bit
            0x1ff,     // 3 9 bit
            0x7f,      // 4 7 bit
            0x1f,      // 5 5 bit
            0xf,       // 6 4 bit
            0xf,       // 7 4 bit
            0x7,       // 8 3 bit        
            0x7,       // 9 3 bit
            0x3,       // 10 2 bit
            0x3,       // 11 2 bit
            0x3,       // 12 2 bit
            0x3,       // 13 2 bit
            0x3,       // 14 2 bit
            0x1,       // 15 1 bit
            0x1,       // 16 1 bit
            0x1,       // 17 1 bit
            0x1,       // 18 1 bit
            0x1,       // 19 1 bit
            0x1,       // 20 1 bit
            0x1,       // 21 1 bit
            0x1,       // 22 1 bit
            0x1,       // 23 1 bit
            0x1,       // 24 1 bit
            0x1,       // 25 1 bit
            0x1,       // 26 1 bit
            0x1,       // 27 1 bit
            0x1,       // 28 1 bit
            0x1,       
    };
    
    private static final int[] flags = {
       1, 2, 3, 4, 5, 5, 6, 6, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 9
    };

    private static int selectScheme(int[] input, int ioffset){
		int cnt = BitUtils.min(input.length - ioffset, 28);
		int max = input[ioffset];
		int i;
		for (i = 1; i<cnt; i++){
			max = BitUtils.max(max, input[ioffset+i]);
			if (max > limits[i]) break;
		}
		return flags[i-1];
	}
	
	
	private static int encodeValue(int code, int cnt, int bitWidth, int ioffset, int ooffset, int[] input, int[] output){
		output[ooffset] = code << 28;
		int limit = BitUtils.min(input.length - ioffset, cnt);
		int pos = 28 - bitWidth;
		for (int i=0; i<limit; i++){
			output[ooffset] |= input[ioffset++] << pos;
			pos -= bitWidth;
		}
		return limit;//The number of integers coded
	}
	
	private static int encodeone(int ioffset, int ooffset, int[] input, int[] output){
    	switch (selectScheme(input, ioffset)){
        	case 1: return encodeValue(8, 1 , 28, ioffset, ooffset, input, output);
            case 2: return encodeValue(7, 2 , 14, ioffset, ooffset, input, output);
            case 3: return encodeValue(6, 3 , 9 , ioffset, ooffset, input, output);
            case 4: return encodeValue(5, 4 , 7 , ioffset, ooffset, input, output);
            case 5: return encodeValue(4, 5 , 5 , ioffset, ooffset, input, output);
            case 6: return encodeValue(3, 7 , 4 , ioffset, ooffset, input, output);
            case 7: return encodeValue(2, 9 , 3 , ioffset, ooffset, input, output);
            case 8: return encodeValue(1, 14, 2 , ioffset, ooffset, input, output);
            default:return encodeValue(0, 28, 1 , ioffset, ooffset, input, output);
        }
    }
	
    private static int decodeValueA(int encoded, int ooffset, int[] output){
        output[ooffset + 0] = (encoded >>> 27) & 1;
        output[ooffset + 1] = (encoded >>> 26) & 1;
        output[ooffset + 2] = (encoded >>> 25) & 1;
        output[ooffset + 3] = (encoded >>> 24) & 1;
        output[ooffset + 4] = (encoded >>> 23) & 1;
        output[ooffset + 5] = (encoded >>> 22) & 1;
        output[ooffset + 6] = (encoded >>> 21) & 1;
        output[ooffset + 7] = (encoded >>> 20) & 1;
        output[ooffset + 8] = (encoded >>> 19) & 1;
        output[ooffset + 9] = (encoded >>> 18) & 1;
        output[ooffset + 10] = (encoded >>> 17) & 1;
        output[ooffset + 11] = (encoded >>> 16) & 1;
        output[ooffset + 12] = (encoded >>> 15) & 1;
        output[ooffset + 13] = (encoded >>> 14) & 1;
        output[ooffset + 14] = (encoded >>> 13) & 1;
        output[ooffset + 15] = (encoded >>> 12) & 1;
        output[ooffset + 16] = (encoded >>> 11) & 1;
        output[ooffset + 17] = (encoded >>> 10) & 1;
        output[ooffset + 18] = (encoded >>> 9) & 1;
        output[ooffset + 19] = (encoded >>> 8) & 1;
        output[ooffset + 20] = (encoded >>> 7) & 1;
        output[ooffset + 21] = (encoded >>> 6) & 1;
        output[ooffset + 22] = (encoded >>> 5) & 1;
        output[ooffset + 23] = (encoded >>> 4) & 1;
        output[ooffset + 24] = (encoded >>> 3) & 1;
        output[ooffset + 25] = (encoded >>> 2) & 1;
        output[ooffset + 26] = (encoded >>> 1) & 1;
        output[ooffset + 27] = encoded & 1;
        return 28;
    }

    private static int decodeValueB(int encoded, int ooffset, int[] output){
        output[ooffset] = (encoded >>> 26) & 3;
        output[ooffset + 1] = (encoded >>> 24) & 3;
        output[ooffset + 2] = (encoded >>> 22) & 3;
        output[ooffset + 3] = (encoded >>> 20) & 3;
        output[ooffset + 4] = (encoded >>> 18) & 3;
        output[ooffset + 5] = (encoded >>> 16) & 3;
        output[ooffset + 6] = (encoded >>> 14) & 3;
        output[ooffset + 7] = (encoded >>> 12) & 3;
        output[ooffset + 8] = (encoded >>> 10) & 3;
        output[ooffset + 9] = (encoded >>> 8) & 3;
        output[ooffset + 10] = (encoded >>> 6) & 3;
        output[ooffset + 11] = (encoded >>> 4) & 3;
        output[ooffset + 12] = (encoded >>> 2) & 3;
        output[ooffset + 13] = encoded & 3;
        return 14;
    }

    private static int decodeValueC(int encoded, int ooffset, int[] output){
        output[ooffset] = (encoded >>> 25) & 7;
        output[ooffset + 1] = (encoded >>> 22) & 7;
        output[ooffset + 2] = (encoded >>> 19) & 7;
        output[ooffset + 3] = (encoded >>> 16) & 7;
        output[ooffset + 4] = (encoded >>> 13) & 7;
        output[ooffset + 5] = (encoded >>> 10) & 7;
        output[ooffset + 6] = (encoded >>> 7) & 7;
        output[ooffset + 7] = (encoded >>> 4) & 7;
        output[ooffset + 8] = (encoded >>> 1) & 7;
        return 9;
    }

    private static int decodeValueD(int encoded, int ooffset, int[] output){
        output[ooffset] = (encoded >>> 24) & 15;
        output[ooffset + 1] = (encoded >>> 20) & 15;
        output[ooffset + 2] = (encoded >>> 16) & 15;
        output[ooffset + 3] = (encoded >>> 12) & 15;
        output[ooffset + 4] = (encoded >>> 8) & 15;
        output[ooffset + 5] = (encoded >>> 4) & 15;
        output[ooffset + 6] = encoded & 15;
        return 7;
    }

    private static int decodeValueE(int encoded, int ooffset, int[] output){
        output[ooffset] = (encoded >>> 23) & 31;
        output[ooffset + 1] = (encoded >>> 18) & 31;
        output[ooffset + 2] = (encoded >>> 13) & 31;
        output[ooffset + 3] = (encoded >>> 8) & 31;
        output[ooffset + 4] = (encoded >>> 3) & 31;
        return 5;
    }

    private static int decodeValueF(int encoded, int ooffset, int[] output){
        output[ooffset] = (encoded >>> 21) & 127;
        output[ooffset + 1] = (encoded >>> 14) & 127;
        output[ooffset + 2] = (encoded >>> 7) & 127;
        output[ooffset + 3] = encoded & 127;
        return 4;
    }

    private static int decodeValueG(int encoded, int outputOffset, int[] output){
        output[outputOffset] = (encoded >>> 19) & 511;
        output[outputOffset + 1] = (encoded >>> 10) & 511;
        output[outputOffset + 2] = (encoded >>> 1) & 511;
        return 3;
    }

    private static int decodeValueH(int encoded, int ooffset, int[] output){
        for (int i = 0, pos = 14; i < 2; i++, pos -= 14){
            output[ooffset + i] = (encoded >>> pos) & 16383;
        }
        return 2;
    }

    private static int decodeValueI(int encoded, int ooffset, int[] output){
        output[ooffset] = encoded & 268435455;
        return 1;
    }
    
    private static int decodeone(int input, int ooffset, int[] output){
    	switch (input >>> 28){
    		case 0: return decodeValueA(input, ooffset, output);
            case 1: return decodeValueB(input, ooffset, output);
            case 2: return decodeValueC(input, ooffset, output);
            case 3: return decodeValueD(input, ooffset, output);
            case 4: return decodeValueE(input, ooffset, output);
            case 5: return decodeValueF(input, ooffset, output);
            case 6: return decodeValueG(input, ooffset, output);
            case 7: return decodeValueH(input, ooffset, output);
            default:return decodeValueI(input, ooffset, output);
        }
    }

	//默认为diffarray.length
	public void encode(int[] diffarray, int difflength, int[] docidout, int[]dcountw) {
        for (int i = 0;
                i < difflength;
                i += Simple9Coding4SkipSimpleBPR.encodeone(i, dcountw[0], diffarray, docidout), dcountw[0]++);
	}
	/**
	 * decode过程必须连续进行才有效
	 * @param buffer 输入数据bytebuffer
	 * @param waste buffer中第一个int解压后需要被忽略几个开头数字
	 * @param diffarray 解压输出数据数组
	 * @param offset diffarray中初始从那个位置开始存储,默认是0
	 * @param length diffarray中一共需要解压多少个元素,
	 * @return 下次丢弃几个数字,本次最后一次解压有效数字的个数
	 */
	public int decode(BufferWrapper buffer, int waste, int[] diffarray, int offset, int length) {
		int decodeno = 0;
		int[] temparray=new int[length+27+waste];//最后一个32bits可能解压为28个数字,另外考虑到上次已经解压的数字也要重复存储
		int i;
		for(i=0; i < length+waste; i += decodeno){
			decodeno = decodeone(buffer.getInt(), i, temparray);
		}
		for(int j=0; j < length; j++){
			diffarray[j+offset]=temparray[j+waste];
		}
		if(i > length+waste){
			buffer.position(buffer.position()-4);//有丢弃的数字，将buffer的位置前移一个int
			return decodeno-(i-length-waste);//下次解压无用的数字,本次最后一次解压有用的数字个数(不是本次解压没用上的数字个数)，
		//可能是256个数字压缩时最后一次压缩中填充的0，也可能是128个docid的末尾几个数字
		}else//i==length+waste
			return 0;
	}


}
