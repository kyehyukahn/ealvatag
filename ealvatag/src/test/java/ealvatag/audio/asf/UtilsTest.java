package ealvatag.audio.asf;

import ealvatag.audio.asf.data.AsfHeader;
import ealvatag.audio.asf.data.GUID;
import ealvatag.audio.asf.util.Utils;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Tests the correctness of the methods in {@link Utils}.<br>
 * Except for methods related to {@link RandomAccessFile}, not worth the effort,
 * since this ASF access is just about to be replaced by streaming.
 *
 * @author Christian Laireiter
 */
public class UtilsTest {

    /**
     * Every bit in this 8 byte array is set.
     */
    public final static byte[] FULL_SET = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};

    /**
     * Max long value as UINT64
     */
    public final static byte[]
            MAX_LONG_64 =
            {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0x7F};

    /**
     * Max UINT16 value.
     */
    public final static byte[] MAX_UINT16 = {(byte)0xFF, (byte)0xFF};

    /**
     * Max UINT32 value.
     */
    public final static byte[] MAX_UINT32 = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#checkStringLengthNullSafe(java.lang.String)}
     */
    @Test public void testCheckStringLengthNullSafe() {
        Utils.checkStringLengthNullSafe(null);
        Utils.checkStringLengthNullSafe("AllOk"); //$NON-NLS-1$
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < 65532 / 2; i++) {
            buffer.append("a");
        }
        Utils.checkStringLengthNullSafe(buffer.toString());
        boolean caught = false;
        try {
            buffer.append("a");
            Utils.checkStringLengthNullSafe(buffer.toString());
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        Assert.assertTrue(caught);
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#getBytes(long, int)}.
     */
    @Test public void testGetBytes() {
        Assert.assertTrue(Arrays.equals(MAX_UINT16, Utils.getBytes(Short.MAX_VALUE * 2 + 1, 2)));
        Assert.assertTrue(Arrays.equals(MAX_UINT32, Utils.getBytes(Integer.MAX_VALUE * 2 + 1, 4)));
        Assert.assertTrue(Arrays.equals(MAX_LONG_64, Utils.getBytes(Long.MAX_VALUE, 8)));
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#isBlank(java.lang.String)}.
     */
    @Test public void testIsBlank() {
        Assert.assertTrue(Utils.isBlank(null));
        Assert.assertTrue(Utils.isBlank("")); //$NON-NLS-1$
        Assert.assertTrue(Utils.isBlank(" 		")); //$NON-NLS-1$
        Assert.assertFalse(Utils.isBlank("a")); //$NON-NLS-1$
        Assert.assertFalse(Utils.isBlank("   a")); //$NON-NLS-1$
        Assert.assertFalse(Utils.isBlank("		a")); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#readBig64(java.io.InputStream)}
     * .
     *
     * @throws IOException Never
     */
    @Test public void testReadBig64InputStream() throws IOException {
        BigInteger big64 = Utils.readBig64(new ByteArrayInputStream(MAX_LONG_64));
        Assert.assertEquals(Long.MAX_VALUE, big64.longValue());
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#readCharacterSizedString(java.io.InputStream)}
     * .
     *
     * @throws IOException On I/O Errors.
     */
    @Test public void testReadCharacterSizedStringInputStream() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String charSized = "This is a TestValue"; //$NON-NLS-1$
        bos.write(Utils.getBytes(charSized.length() + 1, 2));
        bos.write(Utils.getBytes(charSized, AsfHeader.ASF_CHARSET));
        bos.write(AsfHeader.ZERO_TERM);
        Assert.assertEquals(charSized, Utils.readCharacterSizedString(new ByteArrayInputStream(bos.toByteArray())));
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#readGUID(java.io.InputStream)}
     * .
     *
     * @throws IOException Should not
     */
    @Test public void testReadGUIDInputStream() throws IOException {
        for (int i = 0; i < GUID.KNOWN_GUIDS.length; i++) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bos.write(GUID.KNOWN_GUIDS[i].getBytes());
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            GUID readGUID = Utils.readGUID(bis);
            Assert.assertTrue(readGUID.equals(GUID.KNOWN_GUIDS[i]));
        }
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#readUINT16(java.io.InputStream)}
     * .
     *
     * @throws IOException Never
     */
    @Test public void testReadUINT16InputStream() throws IOException {
        long value = Utils.readUINT16(new ByteArrayInputStream(MAX_UINT16));
        Assert.assertEquals(Short.MAX_VALUE * 2 + 1, value);
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#readUINT32(java.io.InputStream)}
     * .
     *
     * @throws IOException Never
     */
    @Test public void testReadUINT32InputStream() throws IOException {
        long value = Utils.readUINT32(new ByteArrayInputStream(MAX_UINT32));
        Assert.assertEquals((long)Integer.MAX_VALUE * 2 + 1, value);
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#readUINT64(java.io.InputStream)}
     * .
     *
     * @throws IOException Never
     */
    @Test public void testReadUINT64InputStream() throws IOException {
        long value = Utils.readUINT64(new ByteArrayInputStream(MAX_LONG_64));
        Assert.assertEquals(Long.MAX_VALUE, value);
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#readUTF16LEStr(java.io.InputStream)}
     * .
     *
     * @throws IOException Never
     */
    @Test public void testReadUTF16LEStrInputStream() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String testValue = "Testvalue"; //$NON-NLS-1$
        // Test with zero term
        Utils.writeUINT16(testValue.length() * 2 + 2, bos);
        bos.write(Utils.getBytes(testValue, AsfHeader.ASF_CHARSET));
        bos.write(AsfHeader.ZERO_TERM);
        String string = Utils.readUTF16LEStr(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(testValue, string);
        bos.reset();
        // Test without zero term
        Utils.writeUINT16(testValue.length() * 2, bos);
        bos.write(Utils.getBytes(testValue, AsfHeader.ASF_CHARSET));
        string = Utils.readUTF16LEStr(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals(testValue, string);
        bos.reset();
        // Test zero length zero term
        Utils.writeUINT16(2, bos);
        bos.write(AsfHeader.ZERO_TERM);
        string = Utils.readUTF16LEStr(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals("", string); //$NON-NLS-1$
        bos.reset();
        // Test zero length
        Utils.writeUINT16(0, bos);
        string = Utils.readUTF16LEStr(new ByteArrayInputStream(bos.toByteArray()));
        Assert.assertEquals("", string); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#writeUINT16(int, java.io.OutputStream)}
     * .
     *
     * @throws IOException Never
     */
    @Test public void testWriteUINT16() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Utils.writeUINT16(Short.MAX_VALUE * 2 + 1, bos);
        Assert.assertTrue(Arrays.equals(MAX_UINT16, bos.toByteArray()));
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#writeUINT32(long, java.io.OutputStream)}
     * .
     *
     * @throws IOException Never
     */
    @Test public void testWriteUINT32() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Utils.writeUINT32((long)Integer.MAX_VALUE * 2 + 1, bos);
        Assert.assertTrue(Arrays.equals(MAX_UINT32, bos.toByteArray()));
    }

    /**
     * Test method for
     * {@link ealvatag.audio.asf.util.Utils#writeUINT64(long, java.io.OutputStream)}
     * .
     *
     * @throws IOException Never
     */
    @Test public void testWriteUINT64() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Utils.writeUINT64(Long.MAX_VALUE, bos);
        Assert.assertTrue(Arrays.equals(MAX_LONG_64, bos.toByteArray()));
    }


}
