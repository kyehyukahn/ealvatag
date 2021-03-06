package ealvatag.issues;

import ealvatag.TestUtil;
import ealvatag.audio.mp3.MP3File;
import ealvatag.tag.id3.ID3v23Frame;
import ealvatag.tag.id3.ID3v23Frames;
import ealvatag.tag.id3.ID3v23Tag;
import ealvatag.tag.id3.framebody.FrameBodyIPLS;
import ealvatag.tag.id3.framebody.FrameBodyTXXX;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Test trying to read non existent mp3 file
 */
public class Issue001Test {
    @After public void tearDown() {
        TestUtil.deleteTestDataTemp();
    }

    @Test public void testHandlingOfUnmappedChars()
    {
        Exception ex=null;
        try
        {
            File testFile = TestUtil.copyAudioToTmp("testV1.mp3", new File("test1001.mp3"));
            MP3File mp3File = new MP3File(testFile);

            //Create and Save
            ID3v23Tag tag = new ID3v23Tag();
            FrameBodyTXXX frameBody = new FrameBodyTXXX();
            frameBody.setDescription(FrameBodyTXXX.MOOD);
            frameBody.setText("\uDFFF");
            ID3v23Frame frame = new ID3v23Frame(ID3v23Frames.FRAME_ID_V3_USER_DEFINED_INFO);
            frame.setBody(frameBody);
            tag.setFrame(frame);
            mp3File.setID3v2Tag(tag);
            mp3File.saveMp3();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ex=e;
        }
        Assert.assertNull(ex);
    }

    @Test public void testHandlingOfUnmappedChars2()
    {
        Exception ex=null;
        try
        {
            File testFile = TestUtil.copyAudioToTmp("testV1.mp3", new File("test1001.mp3"));
            MP3File mp3File = new MP3File(testFile);

            //Create and Save
            ID3v23Tag tag = new ID3v23Tag();
            FrameBodyIPLS frameBody = new FrameBodyIPLS();
            frameBody.setText("producer\0eno,lanois\0engineer\0"+"\uDFFF");
            ID3v23Frame frame = new ID3v23Frame(ID3v23Frames.FRAME_ID_V3_INVOLVED_PEOPLE);
            frame.setBody(frameBody);
            tag.setFrame(frame);
            mp3File.setID3v2Tag(tag);
            mp3File.saveMp3();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ex=e;
        }
        Assert.assertNull(ex);
    }

}
