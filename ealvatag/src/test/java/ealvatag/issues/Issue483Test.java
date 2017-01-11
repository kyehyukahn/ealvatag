package ealvatag.issues;

import ealvatag.AbstractTestCase;
import ealvatag.audio.AudioFile;
import ealvatag.audio.AudioFileIO;

import java.io.File;

/**
 * Test
 */
public class Issue483Test extends AbstractTestCase
{
    public void testCompareMp3Tag() throws Exception
    {
        File orig = new File("testdata", "test113.mp3");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }


        AudioFile af1 = AudioFileIO.read(orig);
        AudioFile af2 = AudioFileIO.read(orig);
        assertNotSame(af1,af2);
        assertEquals(af1.getTag(),af2.getTag());
    }

    public void testCompareMp4Tag() throws Exception
    {
        File orig = new File("testdata", "test.m4a");
        AudioFile af1 = AudioFileIO.read(orig);
        AudioFile af2 = AudioFileIO.read(orig);
        assertNotSame(af1,af2);
        assertNotSame(af1.getTag(),af2.getTag());
    }

    public void testCompareFlacTag() throws Exception
    {
        File orig = new File("testdata", "test.flac");
        AudioFile af1 = AudioFileIO.read(orig);
        AudioFile af2 = AudioFileIO.read(orig);
        assertNotSame(af1,af2);
        assertNotSame(af1.getTag(),af2.getTag());
    }

    public void testCompareOggTag() throws Exception
    {
        File orig = new File("testdata", "test.ogg");
        AudioFile af1 = AudioFileIO.read(orig);
        AudioFile af2 = AudioFileIO.read(orig);
        assertNotSame(af1,af2);
        assertNotSame(af1.getTag(),af2.getTag());
    }

    public void testCompareAifTag() throws Exception
    {
        File orig = new File("testdata", "test132.aif");
        AudioFile af1 = AudioFileIO.read(orig);
        AudioFile af2 = AudioFileIO.read(orig);
        assertNotSame(af1,af2);
        assertNotSame(af1.getTag(),af2.getTag());
    }

    public void testCompareWavTag() throws Exception
    {
        File orig = new File("testdata", "test125.wav");
        AudioFile af1 = AudioFileIO.read(orig);
        AudioFile af2 = AudioFileIO.read(orig);
        assertNotSame(af1,af2);
        assertNotSame(af1.getTag(),af2.getTag());
    }

    public void testCompareWmaTag() throws Exception
    {
        File orig = new File("testdata", "test1.wma");
        AudioFile af1 = AudioFileIO.read(orig);
        AudioFile af2 = AudioFileIO.read(orig);
        assertNotSame(af1,af2);
        assertNotSame(af1.getTag(),af2.getTag());
    }
}