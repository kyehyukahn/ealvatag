package ealvatag.issues;

import ealvatag.TestUtil;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.mp3.MP3File;
import ealvatag.tag.id3.ID3v22Frame;
import ealvatag.tag.id3.ID3v22Frames;
import ealvatag.tag.id3.ID3v22Tag;
import ealvatag.tag.id3.ID3v24Frame;
import ealvatag.tag.id3.ID3v24Frames;
import ealvatag.tag.id3.ID3v24Tag;
import ealvatag.tag.id3.framebody.FrameBodyIPLS;
import ealvatag.tag.id3.framebody.FrameBodyTIPL;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Test Writing to new urls with common interface
 */
public class Issue249Test {
    @After public void tearDown() {
        TestUtil.deleteTestDataTemp();
    }

    /**
     * Test New Urls ID3v24
     */
    @Test public void testConvertv22tagWithIPLSToV24() {
        ID3v24Tag v24tag = null;
        ID3v22Tag v22tag = null;
        File orig = new File("testdata", "test34.mp3");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            //This is a V2 File with an IPL frame
            File testFile = TestUtil.copyAudioToTmp("test34.mp3");

            //Convert to v24Tag
            AudioFile af = AudioFileIO.read(testFile);
            MP3File mp3File = (MP3File)af;
            v24tag = mp3File.getID3v2TagAsv24();
            v22tag = (ID3v22Tag)mp3File.getID3v2Tag();

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
        ID3v24Frame frame = (ID3v24Frame)v24tag.getFrame(ID3v24Frames.FRAME_ID_INVOLVED_PEOPLE);
        ID3v22Frame framev2 = (ID3v22Frame)v22tag.getFrame(ID3v22Frames.FRAME_ID_V2_IPLS);

        Assert.assertNotNull(frame);
        Assert.assertNotNull(framev2);

        //Original
        FrameBodyIPLS framebodyv2 = (FrameBodyIPLS)framev2.getBody();
        Assert.assertEquals(1, framebodyv2.getNumberOfPairs());
        Assert.assertEquals("", framebodyv2.getKeyAtIndex(0));
        Assert.assertEquals("PRAISE J.R. \"BOB\" DOBBS!!!", framebodyv2.getValueAtIndex(0));

        //Converted to v24
        FrameBodyTIPL framebody = (FrameBodyTIPL)frame.getBody();
        Assert.assertEquals(1, framebody.getNumberOfPairs());
        Assert.assertEquals("", framebody.getKeyAtIndex(0));
        Assert.assertEquals("PRAISE J.R. \"BOB\" DOBBS!!!", framebody.getValueAtIndex(0));
    }

}
