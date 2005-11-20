/*
 * $HeadURL$
 * $Revision$
 * $Date$
 *
 * ====================================================================
 *
 *  Copyright 1999-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.io;

/**
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 * 
 * @version $Revision$
 * 
 * @since 4.0
 */
public class CharArrayBuffer  {
    
    private char[] buffer;
    private int len;

    public CharArrayBuffer(int capacity) {
        super();
        if (capacity < 0) {
            throw new IllegalArgumentException("Buffer capacity may not be negative");
        }
        this.buffer = new char[capacity]; 
    }

    private void expand(int newlen) {
    	char newbuffer[] = new char[Math.max(this.buffer.length << 1, newlen)];
        System.arraycopy(this.buffer, 0, newbuffer, 0, this.len);
        this.buffer = newbuffer;
    }
    
    public void append(final char[] b, int off, int len) {
        if (b == null) {
            return;
        }
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) < 0) || ((off + len) > b.length)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        int newlen = this.len + len;
        if (newlen > this.buffer.length) {
        	expand(newlen);
        }
        System.arraycopy(b, off, this.buffer, this.len, len);
        this.len = newlen;
    }
    
    public void append(String str) {
    	if (str == null) {
    	    str = "null";
    	}
    	int strlen = str.length();
    	int newlen = this.len + strlen;
    	if (newlen > this.buffer.length) {
        	expand(newlen);
    	}
    	str.getChars(0, strlen, this.buffer, this.len);
    	this.len = newlen;
    }

    public void append(char ch) {
    	int newlen = this.len + 1;
    	if (newlen > this.buffer.length) {
        	expand(newlen);
    	}
    	this.buffer[this.len] = ch;
    	this.len = newlen;
    }

    public void append(final Object obj) {
    	append(String.valueOf(obj));
    }
    
    public void clear() {
    	this.len = 0;
    }
    
    public char[] internBuffer() {
        return this.buffer;
    }
    
    public char[] toCharArray() {
    	char[] b = new char[this.len]; 
    	if (this.len > 0) {
            System.arraycopy(this.buffer, 0, b, 0, this.len);
    	}
        return b;
    }
    
    public char charAt(int i) {
        return this.buffer[i];
    }
    
    public int capacity() {
        return this.buffer.length;
    }
    
    public int length() {
        return this.len;
    }

    public void ensureCapacity(int required) {
        int available = this.buffer.length - this.len;
        if (required > available) {
            expand(this.len + required);
        }
    }
    
    public void setLength(int len) {
        if (len < 0 || len > this.buffer.length) {
            throw new IndexOutOfBoundsException();
        }
        this.len = len;
    }
    
    public boolean isEmpty() {
        return this.len == 0; 
    }
    
    public int indexOf(int ch, int fromIndex) {
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (fromIndex > this.len) {
            return -1;
        }
        for (int i = fromIndex; i < this.len; i++) {
            if (this.buffer[i] == ch) {
                return i;
            }
        }
        return -1;
    }
    
    public int indexOf(int ch) {
        return indexOf(ch, 0);
    }

    public String substring(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (endIndex > this.len) {
            throw new IndexOutOfBoundsException();
        }
        if (beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        return new String(this.buffer, beginIndex, endIndex - beginIndex);
    }
    
    public String substringTrimmed(int beginIndex, int endIndex) {
        if (beginIndex < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (endIndex > this.len) {
            throw new IndexOutOfBoundsException();
        }
        if (beginIndex > endIndex) {
            throw new IndexOutOfBoundsException();
        }
        while (beginIndex < endIndex && Character.isWhitespace(this.buffer[beginIndex])) {
            beginIndex++;
        }
        while (endIndex > beginIndex && Character.isWhitespace(this.buffer[endIndex - 1])) {
            endIndex--;
        }
        return new String(this.buffer, beginIndex, endIndex - beginIndex);
    }
    
    public String toString() {
    	return new String(this.buffer, 0, this.len);
    }
    
}
