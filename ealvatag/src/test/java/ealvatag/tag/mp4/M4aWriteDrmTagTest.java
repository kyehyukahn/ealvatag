package ealvatag.tag.mp4;

import ealvatag.TestUtil;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.mp4.Mp4AudioHeader;
import ealvatag.audio.mp4.Mp4AudioProfile;
import ealvatag.audio.mp4.Mp4Kind;
import ealvatag.audio.mp4.atom.Mp4StcoBox;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;
import ealvatag.tag.Tag;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Write drms files, we can modify the metadata without breaking the drm file itself
 */
public class M4aWriteDrmTagTest {
    /**
     * Example code of how to show stco table
     *
     */
    @Test public void testShowStco() throws Exception {
        System.out.println("Start");
        try {
            File orig = new File("testdata", "test8.m4a");
            if (!orig.isFile()) {
                System.out.println("File Does not Exist");
                return;
            }
            File testFile = TestUtil.copyAudioToTmp("test8.m4a", new File("test8ReadStco"));


            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            Mp4StcoBox stco = Mp4StcoBox.getStco(raf);
            Assert.assertEquals(496, stco.getNoOfOffSets());
            Assert.assertEquals(56589, stco.getFirstOffSet());
            raf.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Test to write all metadata from an Apple iTunes encoded mp4 file, note also uses fixed genre rather than
     * custom genre
     */
    @Test public void testWriteFile() {
        File orig = new File("testdata", "test9.m4p");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test9.m4p", new File("WriteDrmFile1.m4p"));

            AudioFile f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            tag.setField(FieldKey.ARTIST, "AUTHOR");
            tag.setField(FieldKey.ALBUM, "ALBUM");
            f.save();
            f = AudioFileIO.read(testFile);
            tag = f.getTag().or(NullTag.INSTANCE);

            System.out.println(f.getAudioHeader());
            System.out.println(tag);

            //AudioInfo
            //Time in seconds
            Assert.assertEquals(329, f.getAudioHeader().getDuration(TimeUnit.NANOSECONDS, true));
            Assert.assertEquals(44100, f.getAudioHeader().getSampleRate());
            Assert.assertEquals("2", String.valueOf(f.getAudioHeader().getChannelCount()));
            Assert.assertEquals(128, f.getAudioHeader().getBitRate());

            //MPEG Specific
            Mp4AudioHeader audioheader = (Mp4AudioHeader)f.getAudioHeader();
            Assert.assertEquals(Mp4Kind.MPEG4_AUDIO, audioheader.getKind());
            Assert.assertEquals(Mp4AudioProfile.LOW_COMPLEXITY, audioheader.getProfile());

            //Ease of use methods for common fields
            Assert.assertEquals("AUTHOR", tag.getFirst(FieldKey.ARTIST));
            Assert.assertEquals("ALBUM", tag.getFirst(FieldKey.ALBUM));
            Assert.assertEquals("Simpering Blonde Bombshell", tag.getFirst(FieldKey.TITLE));
            Assert.assertEquals("1990-01-01T08:00:00Z", tag.getFirst(FieldKey.YEAR));
            Assert.assertEquals("1", tag.getFirst(FieldKey.TRACK));
            Assert.assertEquals("12", tag.getFirst(FieldKey.TRACK_TOTAL));
            Assert.assertEquals("Rock", tag.getFirst(FieldKey.GENRE));

            //Cast to format specific tag
            Mp4Tag mp4tag = (Mp4Tag)tag;

            //Lookup by mp4 key
            Assert.assertEquals("AUTHOR", mp4tag.getFirst(Mp4FieldKey.ARTIST));
            Assert.assertEquals("ALBUM", mp4tag.getFirst(Mp4FieldKey.ALBUM));
            Assert.assertEquals("Simpering Blonde Bombshell", mp4tag.getFirst(Mp4FieldKey.TITLE));
            List coverart = mp4tag.get(Mp4FieldKey.ARTWORK);
            //Should be one image
            Assert.assertEquals(1, coverart.size());

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }


}
