package com.ealvatag.tag.vorbiscomment;

import com.ealvatag.AbstractTestCase;
import com.ealvatag.audio.AudioFile;
import com.ealvatag.audio.AudioFileIO;
import com.ealvatag.audio.ogg.OggFileReader;
import com.ealvatag.audio.ogg.util.OggPageHeader;
import com.ealvatag.tag.FieldKey;
import com.ealvatag.tag.TagField;
import com.ealvatag.tag.TagOptionSingleton;
import com.ealvatag.tag.vorbiscomment.util.Base64Coder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Vorbis Write tsgs
 */
public class VorbisWriteTagTest
        extends AbstractTestCase {

    private static final String AUTHOR = "AUTHOR";
    private static final String ALBUM = "ALBUM";
    private static final String TITLE = "title";
    private static final String COMMENTS = "comments";
    private static final String YEAR = "1971";
    private static final String TRACK = "2";
    private static final String GENRE = "Genre";
    private static final String DISC = "1";
    private static final String COMPOSER = "composer\u00A9";
    private static final String SORTARTIST = "Sortartist\u01ff";
    private static final String LYRICS = "lyrics";
    private static final String BPM = "200";
    private static final String ALBUMARTIST = "Albumartist";
    private static final String SORTALBUMARTIST = "Sortalbumartist";
    private static final String SORTALBUM = "Sortalbum";
    private static final String GROUPING = "GROUping";
    private static final String SORTCOMPOSER = "Sortcomposer";
    private static final String SORTTITLE = "sorttitle";
    private static final String IS_COMPILATION = "1";
    private static final String MUSICIP_ID = "66027994-edcf-9d89-bec8-0d30077d888c";
    private static final String MUSICBRAINZ_TRACK_ID = "e785f700-c1aa-4943-bcee-87dd316a2c31";
    private static final String MUSICBRAINZ_ARTIST_ID = "989a13f6-b58c-4559-b09e-76ae0adb94ed";
    private static final String MUSICBRAINZ_RELEASE_ARTIST_ID = "989a13f6-b58c-4559-b09e-76ae0adb94ed";
    private static final String MUSICBRAINZ_RELEASE_ID = "19c6f0f6-3d6d-4b02-88c7-ffb559d52be6";
    private static final String URL_LYRICS_SITE = "http://www.lyrics.fly.com";
    private static final String URL_DISCOGS_ARTIST_SITE = "http://www.discogs1.com";
    private static final String URL_DISCOGS_RELEASE_SITE = "http://www.discogs2.com";
    private static final String URL_ARTIST_SITE = "http://www.discogs3.com";
    private static final String URL_RELEASE_SITE = "http://www.discogs4.com";
    private static final String URL_WIKIPEDIA_ARTIST_SITE = "http://www.discogs5.com";
    private static final String URL_WIKIPEDIA_RELEASE_SITE = "http://www.discogs6.com";
    private static final String TRACK_COUNT = "11";
    private static final String DISC_COUNT = "3";
    private static final String SARAH_CURTIS = "Sarah Curtis";
    private static final String VIOLINIST = "VOLINIST";
    private static final String ENCODER = "encoder";
    private static final String DESCRIPTION = "description";
    private static final String IMAGE_SLASH_PNG = "image/png";

    /**
     * Can summarize file
     */
    public void testSummarizeFile() {
        Exception exceptionCaught = null;
        try {
//Can summarize file
            File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", new File("testSummarizeFile.ogg"));
            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggFileReader oggFileReader = new OggFileReader();
            oggFileReader.summarizeOggPageHeaders(testFile);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Testing of writing various fields
     */
    public void testWriteTagToFile() {
        final String testFileName = "testWriteTagTest.ogg";
        TagOptionSingleton.getInstance().setVorbisAlbumArtistReadOptions(VorbisAlbumArtistReadOptions.READ_ALBUMARTIST);
        TagOptionSingleton.getInstance().setVorbisAlbumArtistSaveOptions(VorbisAlbumArtistSaveOptions.WRITE_ALBUMARTIST);
        Exception exceptionCaught = null;
        try {
            final File copyDestination = new File(testFileName);
            //noinspection ResultOfMethodCallIgnored
            copyDestination.delete();

            File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", copyDestination);
            AudioFile f = AudioFileIO.read(testFile);

            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //These have methods coz common over all formats
            tag.setField(FieldKey.ARTIST, AUTHOR);
            tag.setField(FieldKey.ALBUM, ALBUM);
            tag.setField(FieldKey.TITLE, TITLE);
            tag.setField(FieldKey.COMMENT, COMMENTS);
            tag.setField(FieldKey.YEAR, YEAR);
            tag.setField(FieldKey.TRACK, TRACK);
            tag.setField(FieldKey.GENRE, GENRE);
            //Common keys
            tag.setField(tag.createField(FieldKey.DISC_NO, DISC));
            tag.setField(tag.createField(FieldKey.COMPOSER, COMPOSER));
            tag.setField(tag.createField(FieldKey.ARTIST_SORT, SORTARTIST));
            tag.setField(FieldKey.LYRICS, LYRICS);
            tag.setField(FieldKey.BPM, BPM);
            tag.setField(FieldKey.ALBUM_ARTIST, ALBUMARTIST);
            tag.setField(tag.createField(FieldKey.ALBUM_ARTIST_SORT, SORTALBUMARTIST));
            tag.setField(tag.createField(FieldKey.ALBUM_SORT, SORTALBUM));
            tag.setField(tag.createField(FieldKey.GROUPING, GROUPING));
            tag.setField(tag.createField(FieldKey.COMPOSER_SORT, SORTCOMPOSER));
            tag.setField(tag.createField(FieldKey.TITLE_SORT, SORTTITLE));
            tag.setField(tag.createField(FieldKey.IS_COMPILATION, IS_COMPILATION));
            tag.setField(tag.createField(FieldKey.MUSICIP_ID, MUSICIP_ID));
            tag.setField(tag.createField(FieldKey.MUSICBRAINZ_TRACK_ID, MUSICBRAINZ_TRACK_ID));
            tag.setField(tag.createField(FieldKey.MUSICBRAINZ_ARTISTID, MUSICBRAINZ_ARTIST_ID));
            tag.setField(tag.createField(FieldKey.MUSICBRAINZ_RELEASEARTISTID, MUSICBRAINZ_RELEASE_ARTIST_ID));
            tag.setField(tag.createField(FieldKey.MUSICBRAINZ_RELEASEID, MUSICBRAINZ_RELEASE_ID));
            tag.setField(tag.createField(FieldKey.URL_LYRICS_SITE, URL_LYRICS_SITE));
            tag.setField(tag.createField(FieldKey.URL_DISCOGS_ARTIST_SITE, URL_DISCOGS_ARTIST_SITE));
            tag.setField(tag.createField(FieldKey.URL_DISCOGS_RELEASE_SITE, URL_DISCOGS_RELEASE_SITE));
            tag.setField(tag.createField(FieldKey.URL_OFFICIAL_ARTIST_SITE, URL_ARTIST_SITE));
            tag.setField(tag.createField(FieldKey.URL_OFFICIAL_RELEASE_SITE, URL_RELEASE_SITE));
            tag.setField(tag.createField(FieldKey.URL_WIKIPEDIA_ARTIST_SITE, URL_WIKIPEDIA_ARTIST_SITE));
            tag.setField(tag.createField(FieldKey.URL_WIKIPEDIA_RELEASE_SITE, URL_WIKIPEDIA_RELEASE_SITE));
            tag.setField(tag.createField(FieldKey.TRACK_TOTAL, TRACK_COUNT));
            tag.setField(tag.createField(FieldKey.DISC_TOTAL, DISC_COUNT));


            //Vorbis Only keys
            tag.setField(tag.createField(VorbisCommentFieldKey.DESCRIPTION, DESCRIPTION));

            //tag.setField(tag.createField(FieldKey.ENCODER,"encoder"));
            tag.setVendor(ENCODER);
            assertEquals(ENCODER, tag.getVendor());

            //Add new image, requires two fields in oggVorbis with data in  base64 encoded form
            RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.png"), "r");
            byte[] imagedata = new byte[(int)imageFile.length()];
            imageFile.read(imagedata);
            char[] testdata = Base64Coder.encode(imagedata);
            String base64image = new String(testdata);
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERART, base64image));
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERARTMIME, IMAGE_SLASH_PNG));

            //key not known to ealvatag
            tag.setField(VIOLINIST, SARAH_CURTIS);
            assertEquals(IMAGE_SLASH_PNG, tag.getFirst(VorbisCommentFieldKey.COVERARTMIME));

            f.commit();

            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();
            assertEquals(AUTHOR, tag.getFirst(FieldKey.ARTIST));
            assertEquals(ALBUM, tag.getFirst(FieldKey.ALBUM));
            assertEquals(TITLE, tag.getFirst(FieldKey.TITLE));
            assertEquals(COMMENTS, tag.getFirst(FieldKey.COMMENT));
            assertEquals(YEAR, tag.getFirst(FieldKey.YEAR));
            assertEquals(TRACK, tag.getFirst(FieldKey.TRACK));
            assertEquals(GENRE, tag.getFirst(FieldKey.GENRE));
            assertEquals(AUTHOR, tag.getFirst(FieldKey.ARTIST));
            assertEquals(ALBUM, tag.getFirst(FieldKey.ALBUM));
            assertEquals(TITLE, tag.getFirst(FieldKey.TITLE));
            assertEquals(COMMENTS, tag.getFirst(FieldKey.COMMENT));
            assertEquals(TRACK, tag.getFirst(FieldKey.TRACK));
            assertEquals(DISC, tag.getFirst(FieldKey.DISC_NO));
            assertEquals(COMPOSER, tag.getFirst(FieldKey.COMPOSER));
            assertEquals(SORTARTIST, tag.getFirst(FieldKey.ARTIST_SORT));
            assertEquals(LYRICS, tag.getFirst(FieldKey.LYRICS));
            assertEquals(BPM, tag.getFirst(FieldKey.BPM));
            assertEquals(ALBUMARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST));
            assertEquals(SORTALBUMARTIST, tag.getFirst(FieldKey.ALBUM_ARTIST_SORT));
            assertEquals(SORTALBUM, tag.getFirst(FieldKey.ALBUM_SORT));
            assertEquals(GROUPING, tag.getFirst(FieldKey.GROUPING));
            assertEquals(SORTCOMPOSER, tag.getFirst(FieldKey.COMPOSER_SORT));
            assertEquals(SORTTITLE, tag.getFirst(FieldKey.TITLE_SORT));
            assertEquals(IS_COMPILATION, tag.getFirst(FieldKey.IS_COMPILATION));
            assertEquals(MUSICIP_ID, tag.getFirst(FieldKey.MUSICIP_ID));
            assertEquals(MUSICBRAINZ_TRACK_ID, tag.getFirst(FieldKey.MUSICBRAINZ_TRACK_ID));
            assertEquals(MUSICBRAINZ_ARTIST_ID, tag.getFirst(FieldKey.MUSICBRAINZ_ARTISTID));
            assertEquals(MUSICBRAINZ_RELEASE_ARTIST_ID, tag.getFirst(FieldKey.MUSICBRAINZ_RELEASEARTISTID));
            assertEquals(MUSICBRAINZ_RELEASE_ID, tag.getFirst(FieldKey.MUSICBRAINZ_RELEASEID));
            assertEquals(TRACK_COUNT, tag.getFirst(FieldKey.TRACK_TOTAL));
            assertEquals(DISC_COUNT, tag.getFirst(FieldKey.DISC_TOTAL));

            VorbisCommentTag vorbisTag = tag;
            assertEquals(AUTHOR, vorbisTag.getFirst(VorbisCommentFieldKey.ARTIST));
            assertEquals(ALBUM, vorbisTag.getFirst(VorbisCommentFieldKey.ALBUM));
            assertEquals(TITLE, vorbisTag.getFirst(VorbisCommentFieldKey.TITLE));
            assertEquals(COMMENTS, vorbisTag.getFirst(VorbisCommentFieldKey.COMMENT));
            assertEquals(YEAR, vorbisTag.getFirst(VorbisCommentFieldKey.DATE));
            assertEquals(TRACK, vorbisTag.getFirst(VorbisCommentFieldKey.TRACKNUMBER));
            assertEquals(DISC, vorbisTag.getFirst(VorbisCommentFieldKey.DISCNUMBER));
            assertEquals(COMPOSER, vorbisTag.getFirst(VorbisCommentFieldKey.COMPOSER));
            assertEquals(SORTARTIST, vorbisTag.getFirst(VorbisCommentFieldKey.ARTISTSORT));
            assertEquals(LYRICS, vorbisTag.getFirst(VorbisCommentFieldKey.LYRICS));
            assertEquals(BPM, vorbisTag.getFirst(VorbisCommentFieldKey.BPM));
            assertEquals(ALBUMARTIST, vorbisTag.getFirst(VorbisCommentFieldKey.ALBUMARTIST));
            assertEquals(SORTALBUMARTIST, vorbisTag.getFirst(VorbisCommentFieldKey.ALBUMARTISTSORT));
            assertEquals(SORTALBUM, vorbisTag.getFirst(VorbisCommentFieldKey.ALBUMSORT));
            assertEquals(GROUPING, vorbisTag.getFirst(VorbisCommentFieldKey.GROUPING));
            assertEquals(SORTCOMPOSER, vorbisTag.getFirst(VorbisCommentFieldKey.COMPOSERSORT));
            assertEquals(SORTTITLE, vorbisTag.getFirst(VorbisCommentFieldKey.TITLESORT));
            assertEquals(IS_COMPILATION, vorbisTag.getFirst(VorbisCommentFieldKey.COMPILATION));
            assertEquals(MUSICIP_ID, vorbisTag.getFirst(VorbisCommentFieldKey.MUSICIP_PUID));
            assertEquals(MUSICBRAINZ_TRACK_ID, vorbisTag.getFirst(VorbisCommentFieldKey.MUSICBRAINZ_TRACKID));
            assertEquals(MUSICBRAINZ_ARTIST_ID,
                         vorbisTag.getFirst(VorbisCommentFieldKey.MUSICBRAINZ_ARTISTID));
            assertEquals(MUSICBRAINZ_RELEASE_ARTIST_ID,
                         vorbisTag.getFirst(VorbisCommentFieldKey.MUSICBRAINZ_ALBUMARTISTID));
            assertEquals(MUSICBRAINZ_RELEASE_ID,
                         vorbisTag.getFirst(VorbisCommentFieldKey.MUSICBRAINZ_ALBUMID));
            assertEquals(URL_LYRICS_SITE, tag.getFirst(VorbisCommentFieldKey.URL_LYRICS_SITE));
            assertEquals(URL_DISCOGS_ARTIST_SITE, tag.getFirst(VorbisCommentFieldKey.URL_DISCOGS_ARTIST_SITE));
            assertEquals(URL_DISCOGS_RELEASE_SITE, tag.getFirst(VorbisCommentFieldKey.URL_DISCOGS_RELEASE_SITE));
            assertEquals(URL_ARTIST_SITE, tag.getFirst(VorbisCommentFieldKey.URL_OFFICIAL_ARTIST_SITE));
            assertEquals(URL_RELEASE_SITE, tag.getFirst(VorbisCommentFieldKey.URL_OFFICIAL_RELEASE_SITE));
            assertEquals(URL_WIKIPEDIA_ARTIST_SITE, tag.getFirst(VorbisCommentFieldKey.URL_WIKIPEDIA_ARTIST_SITE));
            assertEquals(URL_WIKIPEDIA_RELEASE_SITE, tag.getFirst(VorbisCommentFieldKey.URL_WIKIPEDIA_RELEASE_SITE));
            assertEquals(TRACK_COUNT, tag.getFirst(VorbisCommentFieldKey.TRACKTOTAL));
            assertEquals(DISC_COUNT, tag.getFirst(VorbisCommentFieldKey.DISCTOTAL));

            assertEquals(SARAH_CURTIS, vorbisTag.getFirst(VIOLINIST));

            assertEquals(ENCODER, vorbisTag.getVendor());

            //List methods
            List<TagField> list = tag.getFields(FieldKey.ARTIST);
            assertEquals(1, list.size());
            for (TagField field : list) {
                assertEquals(AUTHOR, field.toString());
            }

            //Vorbis keys that have no mapping to generic key
            assertEquals(DESCRIPTION, vorbisTag.getFirst(VorbisCommentFieldKey.DESCRIPTION));

            //VorbisImage base64 image, and reconstruct
            assertEquals(IMAGE_SLASH_PNG, vorbisTag.getFirst(VorbisCommentFieldKey.COVERARTMIME));
            assertEquals(base64image, vorbisTag.getFirst(VorbisCommentFieldKey.COVERART));
            BufferedImage bi = ImageIO.read(ImageIO
                                                    .createImageInputStream(new ByteArrayInputStream(Base64Coder.
                                                                                                                        decode(vorbisTag
                                                                                                                                       .getFirst(
                                                                                                                                               VorbisCommentFieldKey.COVERART)
                                                                                                                                       .toCharArray()))));
            assertNotNull(bi);


            OggFileReader ofr = new OggFileReader();
            OggPageHeader oph = ofr.readOggPageHeader(new RandomAccessFile(testFile, "r"), 0);
            assertEquals(30, oph.getPageLength());
            assertEquals(0, oph.getPageSequence());
            assertEquals(559748870, oph.getSerialNumber());
            assertEquals(-2111591604, oph.getCheckSum());
            assertEquals(2, oph.getHeaderType());


        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        } finally {
            new File(testFileName).delete();
        }
        assertNull(exceptionCaught);
    }

    /**
     * Test writing to file, comments too large to fit on single page anymore
     */
    public void testWriteToFileMuchLarger() {
        File orig = new File("testdata", "test.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile =
                    AbstractTestCase.copyAudioToTmp("test.ogg", new File("testWriteTagTestRequiresTwoPages.ogg"));

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Add new image, requires two fields in oggVorbis with data in  base64 encoded form
            RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.bmp"), "r");
            byte[] imagedata = new byte[(int)imageFile.length()];
            imageFile.read(imagedata);
            char[] testdata = Base64Coder.encode(imagedata);
            String base64image = new String(testdata);
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERART, base64image));
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERARTMIME, "image/png"));

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //Check changes
            assertEquals(1, tag.get(VorbisCommentFieldKey.COVERART).size());
            assertEquals(1, tag.get(VorbisCommentFieldKey.COVERARTMIME).size());

            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggFileReader oggFileReader = new OggFileReader();
            System.out.println(oggFileReader.readOggPageHeader(raf, 0));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 1));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 2));
            raf.close();

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Test writing to file, comments too large to fit on single page anymore, and also setup header gets split
     */
    public void testWriteToFileMuchLargerSetupHeaderSplit() {
        File orig = new File("testdata", "test.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = AbstractTestCase.copyAudioToTmp("test.ogg",
                                                            new File("testWriteTagTestRequiresTwoPagesHeaderSplit" +
                                                                             ".ogg"));

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Add new pretend image to force split of setup header
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 128000; i++) {
                sb.append("a");
            }
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERART, sb.toString()));
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERARTMIME, "image/png"));

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //Check changes
            assertEquals(1, tag.get(VorbisCommentFieldKey.COVERART).size());
            assertEquals(1, tag.get(VorbisCommentFieldKey.COVERARTMIME).size());

            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggFileReader oggFileReader = new OggFileReader();
            System.out.println(oggFileReader.readOggPageHeader(raf, 0));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 1));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 2));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 3));

            raf.close();

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Issue 197, test writing to file when audio packet come straight after setup packet on same oggPage, edit so
     * comment data is changed but size of comment header is same length
     */
    public void testWriteToFileWithExtraPacketsOnSamePageAsSetupHeader() {
        File orig = new File("testdata", "test2.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = AbstractTestCase.copyAudioToTmp("test2.ogg",
                                                            new File("testWriteTagWithExtraPacketsHeaderSameSize.ogg"));

            OggFileReader oggFileReader = new OggFileReader();
            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggPageHeader pageHeader = oggFileReader.readOggPageHeader(raf, 1);
            int packetsInSecondPageCount = pageHeader.getPacketList().size();
            raf.close();

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //These have methods coz common over all formats
            tag.setField(FieldKey.ARTIST, "AUTHOR");

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //Check changes
            assertEquals("AUTHOR", tag.getFirst(FieldKey.ARTIST));

            //Check 2nd page has same number of packets, this is only the case for this specific test, so check
            //in test not code itself.
            raf = new RandomAccessFile(testFile, "r");
            pageHeader = oggFileReader.readOggPageHeader(raf, 1);
            raf.close();
            assertEquals(packetsInSecondPageCount, pageHeader.getPacketList().size());


        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Issue 197, test writing to file when audio packet come straight after setup packet on same oggPage, edit enough
     * so that comment is larger, but the comment, header and extra packets can still all fit on page 2
     */
    public void testWriteToFileWithExtraPacketsOnSamePageAsSetupHeaderLarger() {
        File orig = new File("testdata", "test2.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = AbstractTestCase.copyAudioToTmp("test2.ogg",
                                                            new File("testWriteTagWithExtraPacketsHeaderLargerSize" +
                                                                             ".ogg"));

            OggFileReader oggFileReader = new OggFileReader();
            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggPageHeader pageHeader = oggFileReader.readOggPageHeader(raf, 1);
            int packetsInSecondPageCount = pageHeader.getPacketList().size();
            raf.close();

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //These have methods coz common over all formats
            tag.setField(FieldKey.ARTIST, "ARTISTIC");

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //Check changes
            assertEquals("ARTISTIC", tag.getFirst(FieldKey.ARTIST));

            //Check 2nd page has same number of packets, this is only the case for this specific test, so check
            //in test not code itself.
            raf = new RandomAccessFile(testFile, "r");
            pageHeader = oggFileReader.readOggPageHeader(raf, 1);
            raf.close();
            assertEquals(packetsInSecondPageCount, pageHeader.getPacketList().size());

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Issue 197, test writing to file when audio packet come straight after setup packet on same oggPage, edit enough
     * so that comment is much larger, so that comment, header and extra packets can no longer fit on page 2
     */
    public void testWriteToFileWithExtraPacketsOnSamePageAsSetupHeaderMuchLarger() {
        File orig = new File("testdata", "test2.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = AbstractTestCase.copyAudioToTmp("test.ogg",
                                                            new File(
                                                                    "testWriteTagWithExtraPacketsHeaderMuchLargerSize" +
                                                                            ".ogg"));

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Add new image, requires two fields in oggVorbis with data in  base64 encoded form
            RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.bmp"), "r");
            byte[] imagedata = new byte[(int)imageFile.length()];
            imageFile.read(imagedata);
            char[] testdata = Base64Coder.encode(imagedata);
            String base64image = new String(testdata);
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERART, base64image));
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERARTMIME, "image/png"));

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //Check changes
            assertEquals(1, tag.get(VorbisCommentFieldKey.COVERART).size());
            assertEquals(1, tag.get(VorbisCommentFieldKey.COVERARTMIME).size());

            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggFileReader oggFileReader = new OggFileReader();
            System.out.println(oggFileReader.readOggPageHeader(raf, 0));
            raf.seek(0);
            System.out.println("Page 2" + oggFileReader.readOggPageHeader(raf, 1));
            //System.out.println("Page 3"+oggFileReader.readOggPageHeader(raf,2));
            //oggFileReader.readOggPageHeader(raf,4);
            raf.close();

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Issue 197, test writing to file when audio packet come straight after setup packet on same oggPage, edit enough
     * so that comment is much larger, so that comment, header and extra packets can no longer fit on page 2 AND
     * setup header is also split over two
     */
    public void testWriteToFileWithExtraPacketsOnSamePageAsSetupHeaderMuchLargerAndSplit() {
        File orig = new File("testdata", "test2.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = AbstractTestCase.copyAudioToTmp("test2.ogg",
                                                            new File(
                                                                    "testWriteTagWithExtraPacketsHeaderMuchLargerSizeAndSplit.ogg"));

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Add new pretend image to force split of setup header
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 128000; i++) {
                sb.append("a");
            }
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERART, sb.toString()));
            tag.setField(tag.createField(VorbisCommentFieldKey.COVERARTMIME, "image/png"));

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);

            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggFileReader oggFileReader = new OggFileReader();
            System.out.println(oggFileReader.readOggPageHeader(raf, 0));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 1));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 2));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 3));

            raf.close();

            //tag = (VorbisCommentTag)f.getTag();

            //Check changes
            //assertEquals(1,tag.getFields(VorbisCommentFieldKey.COVERART).size());
            //assertEquals(1,tag.getFields(VorbisCommentFieldKey.COVERARTMIME).size());


        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Test writing to file, comments was too large for one page but not anymore
     */
    public void testWriteToFileNoLongerRequiresTwoPages() {
        File orig = new File("testdata", "test3.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = AbstractTestCase.copyAudioToTmp("test3.ogg",
                                                            new File("testWriteTagTestNoLongerRequiresTwoPages.ogg"));

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Delete Large Image
            tag.deleteField(VorbisCommentFieldKey.COVERART);
            tag.deleteField(VorbisCommentFieldKey.COVERARTMIME);

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //Check changes
            assertEquals(0, tag.get(VorbisCommentFieldKey.COVERART).size());
            assertEquals(0, tag.get(VorbisCommentFieldKey.COVERARTMIME).size());

            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggFileReader oggFileReader = new OggFileReader();
            System.out.println(oggFileReader.readOggPageHeader(raf, 0));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 1));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 2));
            raf.close();

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }


    /**
     * Test writing to file, comments was too large for one page and setup header split but not anymore
     */
    public void testWriteToFileNoLongerRequiresTwoPagesNorSplit() {
        File orig = new File("testdata", "test5.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = AbstractTestCase.copyAudioToTmp("test5.ogg",
                                                            new File(
                                                                    "testWriteTagTestNoLongerRequiresTwoPagesNorSplit" +
                                                                            ".ogg"));

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Delete Large Image
            tag.deleteField(VorbisCommentFieldKey.COVERART);
            tag.deleteField(VorbisCommentFieldKey.COVERARTMIME);

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //Check changes
            assertEquals(0, tag.get(VorbisCommentFieldKey.COVERART).size());
            assertEquals(0, tag.get(VorbisCommentFieldKey.COVERARTMIME).size());

            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggFileReader oggFileReader = new OggFileReader();
            System.out.println(oggFileReader.readOggPageHeader(raf, 0));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 1));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 2));
            raf.close();

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Test writing to file, comments was too large for one page but not anymore
     */
    public void testWriteToFileWriteToFileWithExtraPacketsNoLongerRequiresTwoPages() {
        File orig = new File("testdata", "test4.ogg");
        if (!orig.isFile()) {
            return;
        }

        Exception exceptionCaught = null;
        try {
            File testFile = AbstractTestCase.copyAudioToTmp("test4.ogg",
                                                            new File(
                                                                    "testWriteTagTestWithPacketsNoLongerRequiresTwoPages.ogg"));

            AudioFile f = AudioFileIO.read(testFile);
            assertTrue(f.getTag() instanceof VorbisCommentTag);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Delete Large Image
            tag.deleteField(VorbisCommentFieldKey.ARTIST);
            tag.deleteField(VorbisCommentFieldKey.COVERART);
            tag.deleteField(VorbisCommentFieldKey.COVERARTMIME);

            //Save
            f.commit();

            //Reread
            f = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //Check changes
            assertEquals(0, tag.get(VorbisCommentFieldKey.ARTIST).size());
            assertEquals(0, tag.get(VorbisCommentFieldKey.COVERART).size());
            assertEquals(0, tag.get(VorbisCommentFieldKey.COVERARTMIME).size());

            RandomAccessFile raf = new RandomAccessFile(testFile, "r");
            OggFileReader oggFileReader = new OggFileReader();
            System.out.println(oggFileReader.readOggPageHeader(raf, 0));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 1));
            raf.seek(0);
            System.out.println(oggFileReader.readOggPageHeader(raf, 2));
            raf.close();

        } catch (Exception e) {
            e.printStackTrace();
            exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    public void testDeleteTag() throws Exception {
        File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", new File("testDelete.ogg"));
        AudioFile f = AudioFileIO.read(testFile);
        f.setTag(VorbisCommentTag.createNewTag());
        f.commit();

        f = AudioFileIO.read(testFile);
        assertTrue(f.getTag().isEmpty());
        assertEquals("ealvatag", ((VorbisCommentTag)f.getTag()).getVendor());
    }

    public void testDeleteTag2() throws Exception {
        File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", new File("testDelete2.ogg"));
        AudioFile f = AudioFileIO.read(testFile);
        AudioFileIO.delete(f);

        f = AudioFileIO.read(testFile);
        assertTrue(f.getTag().isEmpty());
        assertEquals("ealvatag", ((VorbisCommentTag)f.getTag()).getVendor());
    }

    public void testWriteMultipleFields() throws Exception {
        TagOptionSingleton.getInstance().setVorbisAlbumArtistReadOptions(VorbisAlbumArtistReadOptions.READ_ALBUMARTIST);
        TagOptionSingleton.getInstance()
                          .setVorbisAlbumArtistSaveOptions(VorbisAlbumArtistSaveOptions.WRITE_ALBUMARTIST);


        File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", new File("testWriteMultiple.ogg"));
        AudioFile f = AudioFileIO.read(testFile);
        f.getTag().addField(FieldKey.ALBUM_ARTIST, "artist1");
        f.getTag().addField(FieldKey.ALBUM_ARTIST, "artist2");
        f.commit();
        f = AudioFileIO.read(testFile);
        List<TagField> tagFields = f.getTag().getFields(FieldKey.ALBUM_ARTIST);
        assertEquals(tagFields.size(), 2);
    }

    public void testDeleteFields() throws Exception {
        //Delete using generic key
        File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", new File("testDeleteFields.ogg"));
        AudioFile f = AudioFileIO.read(testFile);
        List<TagField> tagFields = f.getTag().getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());
        f.getTag().addField(FieldKey.ALBUM_ARTIST_SORT, "artist1");
        f.getTag().addField(FieldKey.ALBUM_ARTIST_SORT, "artist2");
        tagFields = f.getTag().getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(2, tagFields.size());
        f.getTag().deleteField(FieldKey.ALBUM_ARTIST_SORT);
        f.commit();

        //Delete using flac id
        f = AudioFileIO.read(testFile);
        tagFields = f.getTag().getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());
        f.getTag().addField(FieldKey.ALBUM_ARTIST_SORT, "artist1");
        f.getTag().addField(FieldKey.ALBUM_ARTIST_SORT, "artist2");
        tagFields = f.getTag().getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(2, tagFields.size());
        f.getTag().deleteField("ALBUMARTISTSORT");
        tagFields = f.getTag().getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());
        f.commit();

        f = AudioFileIO.read(testFile);
        tagFields = f.getTag().getFields(FieldKey.ALBUM_ARTIST_SORT);
        assertEquals(0, tagFields.size());

    }
}
