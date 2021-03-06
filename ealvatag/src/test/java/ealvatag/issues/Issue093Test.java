package ealvatag.issues;

import ealvatag.TestUtil;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;
import ealvatag.audio.AudioHeader;
import ealvatag.audio.aiff.AiffAudioHeader;
import ealvatag.audio.wav.WavOptions;
import ealvatag.audio.wav.WavSaveOptions;
import ealvatag.tag.FieldKey;
import ealvatag.tag.NullTag;
import ealvatag.tag.Tag;
import ealvatag.tag.TagOptionSingleton;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * #119:https://bitbucket.org/ijabz/jaudiotagger/issues/119/wav-aiff-add-padding-byte-if-missing-in
 */
public class Issue093Test {
    @After public void tearDown() {
        TestUtil.deleteTestDataTemp();
    }

    @Test public void testWriteAiffWithCorruptID3Tag1() {
        Exception exceptionCaught = null;

        File orig = new File("testdata", "test145.aiff");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }


        File testFile = TestUtil.copyAudioToTmp("test145.aiff", new File("test145CorruptedID3.aiff"));
        try {
            AudioFile f = AudioFileIO.read(testFile);
            AudioHeader ah = f.getAudioHeader();
            Assert.assertTrue(ah instanceof AiffAudioHeader);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "Jonathon");
            f.save();
            f = AudioFileIO.read(testFile);
            tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("Jonathon", tag.getFirst(FieldKey.ARTIST));
        } catch (Exception ex) {
            ex.printStackTrace();
            exceptionCaught = ex;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testWriteAiffWithCorruptID3Tag2() {

        Exception exceptionCaught = null;

        File orig = new File("testdata", "test152.aiff");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }


        File testFile = TestUtil.copyAudioToTmp("test152.aiff", new File("test152MissingByteId3.aiff"));
        try {
            AudioFile f = AudioFileIO.read(testFile);
            AudioHeader ah = f.getAudioHeader();
            Assert.assertTrue(ah instanceof AiffAudioHeader);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception ex) {
            ex.printStackTrace();
            exceptionCaught = ex;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testNaimRipMultipleTagsFixId3() {
        File orig = new File("testdata", "test152.wav");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }

        TagOptionSingleton.getInstance().setWavOptions(WavOptions.READ_ID3_ONLY);
        TagOptionSingleton.getInstance().setWavSaveOptions(WavSaveOptions.SAVE_ACTIVE);

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test152.wav", new File("test152_id3.wav"));
            AudioFile f = AudioFileIO.read(testFile);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testNaimRipMultipleFixTagsInfo() {
        File orig = new File("testdata", "test152.wav");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }

        TagOptionSingleton.getInstance().setWavOptions(WavOptions.READ_INFO_ONLY);
        TagOptionSingleton.getInstance().setWavSaveOptions(WavSaveOptions.SAVE_ACTIVE);

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test152.wav", new File("test152_info.wav"));
            AudioFile f = AudioFileIO.read(testFile);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testNaimRipMultipleFixTagsExistingId3() {
        File orig = new File("testdata", "test152.wav");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }

        TagOptionSingleton.getInstance().setWavOptions(WavOptions.READ_ID3_ONLY);
        TagOptionSingleton.getInstance().setWavSaveOptions(WavSaveOptions.SAVE_EXISTING_AND_ACTIVE);

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test152.wav", new File("test152_existing_id3.wav"));
            AudioFile f = AudioFileIO.read(testFile);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testNaimRipMultipleFixTagsExistingInfo() {
        File orig = new File("testdata", "test152.wav");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }

        TagOptionSingleton.getInstance().setWavOptions(WavOptions.READ_INFO_ONLY);
        TagOptionSingleton.getInstance().setWavSaveOptions(WavSaveOptions.SAVE_EXISTING_AND_ACTIVE);

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test152.wav", new File("test152_existing_info.wav"));
            AudioFile f = AudioFileIO.read(testFile);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testNaimRipMultipleTagsFixId3Both() {
        File orig = new File("testdata", "test152.wav");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }

        TagOptionSingleton.getInstance().setWavOptions(WavOptions.READ_ID3_ONLY);
        TagOptionSingleton.getInstance().setWavSaveOptions(WavSaveOptions.SAVE_BOTH);

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test152.wav", new File("test152_existing_id3_both.wav"));
            AudioFile f = AudioFileIO.read(testFile);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testNaimRipMultipleFixTagsInfoBoth() {
        File orig = new File("testdata", "test152.wav");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }

        TagOptionSingleton.getInstance().setWavOptions(WavOptions.READ_INFO_ONLY);
        TagOptionSingleton.getInstance().setWavSaveOptions(WavSaveOptions.SAVE_BOTH);

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test152.wav", new File("test152_existing_info_both.wav"));
            AudioFile f = AudioFileIO.read(testFile);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testNaimRipMultipleTagsFixId3BothSync() {
        File orig = new File("testdata", "test152.wav");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }

        TagOptionSingleton.getInstance().setWavOptions(WavOptions.READ_ID3_ONLY);
        TagOptionSingleton.getInstance().setWavSaveOptions(WavSaveOptions.SAVE_BOTH_AND_SYNC);

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test152.wav", new File("test152_existing_id3_both_sync.wav"));
            AudioFile f = AudioFileIO.read(testFile);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }

    @Test public void testNaimRipMultipleFixTagsInfoBothSync() {
        File orig = new File("testdata", "test152.wav");
        if (!orig.isFile()) {
            System.err.println("Unable to test file - not available " + orig);
            return;
        }

        TagOptionSingleton.getInstance().setWavOptions(WavOptions.READ_INFO_ONLY);
        TagOptionSingleton.getInstance().setWavSaveOptions(WavSaveOptions.SAVE_BOTH_AND_SYNC);

        Exception exceptionCaught = null;
        try {
            File testFile = TestUtil.copyAudioToTmp("test152.wav", new File("test152_existing_info_both_sync.wav"));
            AudioFile f = AudioFileIO.read(testFile);
            f.getTag().or(NullTag.INSTANCE).setField(FieldKey.ARTIST, "fred");
            f.save();
            f = AudioFileIO.read(testFile);
            Tag tag = f.getTag().or(NullTag.INSTANCE);
            Assert.assertEquals("fred", tag.getFirst(FieldKey.ARTIST));

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        Assert.assertNull(exceptionCaught);
    }
}
