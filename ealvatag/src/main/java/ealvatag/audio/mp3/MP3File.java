/*
 * @author : Paul Taylor
 * @author : Eric Farng
 * <p>
 * Version @version:$Id$
 * <p>
 * MusicTag Copyright (C)2003,2004
 * <p>
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 * you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package ealvatag.audio.mp3;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.ealva.ealvalog.Logger;
import com.ealva.ealvalog.Loggers;
import ealvatag.audio.AudioFileImpl;
import ealvatag.audio.UnsupportedFileType;
import ealvatag.audio.exceptions.CannotReadException;
import ealvatag.audio.exceptions.CannotWriteException;
import ealvatag.audio.exceptions.InvalidAudioFrameException;
import ealvatag.audio.exceptions.NoWritePermissionsException;
import ealvatag.audio.exceptions.UnableToModifyFileException;
import ealvatag.audio.io.FileOperator;
import ealvatag.logging.AbstractTagDisplayFormatter;
import ealvatag.logging.ErrorMessage;
import ealvatag.logging.Hex;
import ealvatag.logging.Log;
import ealvatag.logging.PlainTextTagDisplayFormatter;
import ealvatag.logging.XMLTagDisplayFormatter;
import ealvatag.tag.Tag;
import ealvatag.tag.TagException;
import ealvatag.tag.TagFieldContainer;
import ealvatag.tag.TagNotFoundException;
import ealvatag.tag.TagOptionSingleton;
import ealvatag.tag.id3.AbstractID3v2Tag;
import ealvatag.tag.id3.BaseID3Tag;
import ealvatag.tag.id3.ID3v11Tag;
import ealvatag.tag.id3.ID3v1Tag;
import ealvatag.tag.id3.ID3v22Tag;
import ealvatag.tag.id3.ID3v23Tag;
import ealvatag.tag.id3.ID3v24Tag;
import ealvatag.tag.id3.Id3v2Header;
import ealvatag.tag.lyrics3.AbstractLyrics3;
import okio.Buffer;

import static com.google.common.base.Preconditions.checkState;
import static com.ealva.ealvalog.LogLevel.DEBUG;
import static com.ealva.ealvalog.LogLevel.ERROR;
import static com.ealva.ealvalog.LogLevel.TRACE;
import static com.ealva.ealvalog.LogLevel.WARN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Locale;

/**
 * This class represents a physical MP3 File
 */
public class MP3File extends AudioFileImpl {
  /* Load ID3V1tag if exists */
  static final int LOAD_IDV1TAG = 2;
  /* Load ID3V2tag if exists */
  static final int LOAD_IDV2TAG = 4;
  /**
   * This option is currently ignored
   */
  private static final int LOAD_LYRICS3 = 8;

  private static final int LOAD_ALL = LOAD_IDV1TAG | LOAD_IDV2TAG | LOAD_LYRICS3;
  private static final Logger LOG = Loggers.INSTANCE.get(Log.MARKER);
  private static final int MINIMUM_FILESIZE = 150;

  private static AbstractTagDisplayFormatter tagFormatter;
  /**
   * the ID3v2 tag that this file contains.
   */
  private AbstractID3v2Tag id3v2tag = null;
  /**
   * Representation of the idv2 tag as a idv24 tag
   */
  private ID3v24Tag id3v2Asv24tag = null;
  /**
   * The Lyrics3 tag that this file contains.
   */
  private AbstractLyrics3 lyrics3tag = null;
  /**
   * The ID3v1 tag that this file contains.
   */
  private ID3v1Tag id3v1tag = null;

  public static AbstractTagDisplayFormatter getStructureFormatter() {
    return tagFormatter;
  }

  @VisibleForTesting
  public MP3File(File file) throws IOException,
                                   TagException,
                                   CannotReadException,
                                   InvalidAudioFrameException,
                                   NoWritePermissionsException {
    this(file, Files.getFileExtension(file.getName()), LOAD_ALL, false);
  }

  /**
   * Creates a new MP3File dataType and parse the tag from the given file
   * Object, files can be opened read only if required.
   *
   * @param file          MP3 file
   * @param extension     always "mp3"? maybe, but we parsed of the extension to select the reader, so let's pass it in.
   * @param loadOptions   decide what tags to load
   * @param ignoreArtwork ignore any artwork fields. Causes the AudioFile to be opened readonly
   *
   * @throws IOException                on any I/O error
   * @throws TagException               on any exception generated by this library.
   * @throws InvalidAudioFrameException error reading frame
   */
  public MP3File(File file,
                 final String extension,
                 int loadOptions,
                 boolean ignoreArtwork) throws IOException, TagException, CannotReadException, InvalidAudioFrameException {
    super(file, extension);
    try (FileChannel fileChannel = getReadFileChannel(file)) {
      FileOperator fileOperator = new FileOperator(fileChannel);
      long audioStart = 0;
      Optional<Id3v2Header> v2HeaderOptional = Optional.absent();
      if ((loadOptions & LOAD_IDV2TAG) != 0) {
        v2HeaderOptional = getV2Header(fileOperator);
      }
      final int v2TagHeaderSize = AbstractID3v2Tag.TAG_HEADER_LENGTH;
      if (v2HeaderOptional.isPresent()) {
        audioStart = v2HeaderOptional.get().getTagSize() + v2TagHeaderSize;
        MP3AudioHeader mp3AudioHeader = new MP3AudioHeader(fileOperator, audioStart, file.getPath());

        //If the audio header is not straight after the end of the tag then search from start of file
        if (audioStart != mp3AudioHeader.getMp3StartByte()) {
          LOG.log(TRACE, "First header found after tag:%s", mp3AudioHeader);
          mp3AudioHeader = checkAudioStart(fileOperator, audioStart, mp3AudioHeader, file.getPath());
          audioStart = mp3AudioHeader.getMp3StartByte();
        }
        audioHeader = mp3AudioHeader;
      } else {
        audioHeader = new MP3AudioHeader(fileOperator, audioStart, file.getPath());
      }

      if (v2HeaderOptional.isPresent()) {
        final Id3v2Header header = v2HeaderOptional.get();
        Buffer buffer = new Buffer();
        // TODO: 1/26/17 Remove the "- v2TaqHeaderSize" from the number of bytes read to see about some tag data reading too far
        fileOperator.read(v2TagHeaderSize, buffer, audioStart - v2TagHeaderSize);
        switch (header.getMajorVersion()) {
          case ID3v22Tag.MAJOR_VERSION:
            setID3v2Tag(new ID3v22Tag(buffer, header, file.getPath(), ignoreArtwork));
            break;
          case ID3v23Tag.MAJOR_VERSION:
            setID3v2Tag(new ID3v23Tag(buffer, header, file.getPath(), ignoreArtwork));
            break;
          case ID3v24Tag.MAJOR_VERSION:
            setID3v2Tag(new ID3v24Tag(buffer, header, file.getPath(), ignoreArtwork));
            break;
        }
      }

      //Read v1 tags (if any)
      readV1Tag(file.getPath(), fileOperator, loadOptions);

      //If we have a v2 tag use that, if we do not but have v1 tag use that
      //otherwise use nothing
      //TODO:if have both should we merge
      //rather than just returning specific ID3v22 tag, would it be better to return v24 version ?
      if (this.getID3v2Tag() != null) {
        tag = this.getID3v2Tag();
      } else if (id3v1tag != null) {
        tag = id3v1tag;
      }


      checkState(!Strings.isNullOrEmpty(extension));
      checkState(audioHeader != null);
    }
  }

  private Optional<Id3v2Header> getV2Header(final FileOperator fileOperator) throws IOException {
    Buffer buffer = new Buffer();
    fileOperator.read(0, buffer, AbstractID3v2Tag.TAG_HEADER_LENGTH);
    return AbstractID3v2Tag.getHeader(buffer);
  }

  private void readV1Tag(String fileName, FileOperator newFile, int loadOptions) throws IOException {
    if ((loadOptions & LOAD_IDV1TAG) != 0) {
      LOG.log(DEBUG, "Attempting to read id3v1tags");


      try {
        id3v1tag = new ID3v11Tag(newFile, fileName);
      } catch (TagNotFoundException ex) {
        LOG.log(TRACE, "No ids3v11 tag found");
      }

      try {
        if (id3v1tag == null) {
          id3v1tag = new ID3v1Tag(newFile, fileName);
        }
      } catch (TagNotFoundException ex) {
        LOG.log(TRACE, "No id3v1 tag found");
      }
    }
  }


  /**
   * Regets the audio header starting from start of file, and write appropriate logging to indicate
   * potential problem to user.
   */
  private MP3AudioHeader checkAudioStart(final FileOperator fileOperator,
                                         long startByte,
                                         MP3AudioHeader firstHeaderAfterTag,
                                         final String filePath) throws IOException, InvalidAudioFrameException {
    MP3AudioHeader headerOne;
    MP3AudioHeader headerTwo;

    LOG.log(WARN, ErrorMessage.MP3_ID3TAG_LENGTH_INCORRECT,
            filePath,
            Hex.asHex(startByte),
            Hex.asHex(firstHeaderAfterTag.getMp3StartByte()));

    //because we cant agree on start location we reread the audioheader from the start of the file, at least
    //this way we cant overwrite the audio although we might overwrite part of the tag if we write this file
    //back later
    headerOne = new MP3AudioHeader(fileOperator, 0, filePath);
    LOG.log(TRACE, "Checking from start:%s", headerOne);

    //Although the id3 tag size appears to be incorrect at least we have found the same location for the start
    //of audio whether we start searching from start of file or at the end of the alleged of file so no real
    //problem
    if (firstHeaderAfterTag.getMp3StartByte() == headerOne.getMp3StartByte()) {
      LOG.log(TRACE, ErrorMessage.MP3_START_OF_AUDIO_CONFIRMED, filePath, Hex.asHex(headerOne.getMp3StartByte()));
      return firstHeaderAfterTag;
    } else {

      // TODO: 2/6/17 There is currently no test that executes this code path

      //We get a different value if read from start, can't guarantee 100% correct lets do some more checks
      LOG.log(TRACE, ErrorMessage.MP3_RECALCULATED_POSSIBLE_START_OF_MP3_AUDIO, filePath, Hex.asHex(headerOne.getMp3StartByte()));

      //Same frame count so probably both audio headers with newAudioHeader being the first one
      if (firstHeaderAfterTag.getNumberOfFrames() == headerOne.getNumberOfFrames()) {
        LOG.log(WARN, ErrorMessage.MP3_RECALCULATED_START_OF_MP3_AUDIO, filePath, Hex.asHex(headerOne.getMp3StartByte()));
        return headerOne;
      }

      //If the size reported by the tag header is a little short and there is only nulls between the recorded
      // value
      //and the start of the first audio found then we stick with the original header as more likely that
      // currentHeader
      //DataInputStream not really a header
      if (isFilePortionNull((int)startByte, (int)firstHeaderAfterTag.getMp3StartByte())) {
        return firstHeaderAfterTag;
      }

      //Skip to the next header (header 2, counting from start of file)
      headerTwo = new MP3AudioHeader(fileOperator,
                                     headerOne.getMp3StartByte() + headerOne.mp3FrameHeader.getFrameLength(),
                                     filePath);

      //It matches the header we found when doing the original search from after the ID3Tag therefore it
      //seems that newAudioHeader was a false match and the original header was correct
      if (headerTwo.getMp3StartByte() == firstHeaderAfterTag.getMp3StartByte()) {
        LOG.log(WARN, ErrorMessage.MP3_START_OF_AUDIO_CONFIRMED, filePath, Hex.asHex(firstHeaderAfterTag.getMp3StartByte()));
        return firstHeaderAfterTag;
      }

      //It matches the frameCount the header we just found so lends weight to the fact that the audio does
      // indeed start at new header
      //however it maybe that neither are really headers and just contain the same data being misrepresented as
      // headers.
      if (headerTwo.getNumberOfFrames() == headerOne.getNumberOfFrames()) {
        LOG.log(WARN, ErrorMessage.MP3_RECALCULATED_START_OF_MP3_AUDIO, filePath, Hex.asHex(headerOne.getMp3StartByte()));
        return headerOne;
      }
      ///Doesn't match the frameCount lets go back to the original header
      else {
        LOG.log(WARN, ErrorMessage.MP3_RECALCULATED_START_OF_MP3_AUDIO, filePath, Hex.asHex(firstHeaderAfterTag.getMp3StartByte()));
        return firstHeaderAfterTag;
      }
    }
  }

  /**
   * @return true if all the bytes between in the file between startByte and endByte are null, false otherwise
   *
   * @throws IOException if read error
   */
  private boolean isFilePortionNull(int startByte, int endByte) throws IOException {
    LOG.log(TRACE, "Checking file portion:%s:%s", Hex.asHex(startByte), Hex.asHex(endByte));
    FileInputStream fis = null;
    FileChannel fc = null;
    try {
      fis = new FileInputStream(file);
      fc = fis.getChannel();
      fc.position(startByte);
      ByteBuffer bb = ByteBuffer.allocateDirect(endByte - startByte);
      fc.read(bb);
      while (bb.hasRemaining()) {
        if (bb.get() != 0) {
          return false;
        }
      }
    } finally {
      if (fc != null) {
        fc.close();
      }

      if (fis != null) {
        fis.close();
      }
    }
    return true;
  }

  public AbstractID3v2Tag getID3v2Tag() {
    return id3v2tag;
  }

  /**
   * Sets the v2 tag to the v2 tag provided as an argument.
   * Also store a v24 version of tag as v24 is the interface to be used
   * when talking with client applications.
   */
  public void setID3v2Tag(AbstractID3v2Tag id3v2tag) {
    this.id3v2tag = id3v2tag;
  }

//  /**
//   * Read lyrics3 Tag
//   * <p>
//   * TODO:not working
//   *
//   */
//  private void readLyrics3Tag(File file, RandomAccessFile newFile, int loadOptions) throws IOException {
//        /*if ((loadOptions & LOAD_LYRICS3) != 0)
//        {
//            try
//            {
//                lyrics3tag = new Lyrics3v2(newFile);
//            }
//            catch (TagNotFoundException ex)
//            {
//            }
//            try
//            {
//                if (lyrics3tag == null)
//                {
//                    lyrics3tag = new Lyrics3v1(newFile);
//                }
//            }
//            catch (TagNotFoundException ex)
//            {
//            }
//        }
//        */
//  }

  /**
   * Extracts the raw ID3v2 tag data into a file.
   * <p>
   * This provides access to the raw data before manipulation, the data is written from the start of the file
   * to the start of the Audio Data. This is primarily useful for manipulating corrupted tags that are not
   * (fully) loaded using the standard methods.
   *
   * @param outputFile to write the data to
   */
  public File extractID3v2TagDataIntoFile(File outputFile) throws TagNotFoundException, IOException {
    int startByte = (int)((MP3AudioHeader)audioHeader).getMp3StartByte();
    if (startByte >= 0) {

      //Read byte into buffer
      FileInputStream fis = new FileInputStream(file);
      FileChannel fc = fis.getChannel();
      ByteBuffer bb = ByteBuffer.allocate(startByte);
      fc.read(bb);

      //Write bytes to outputFile
      FileOutputStream out = new FileOutputStream(outputFile);
      out.write(bb.array());
      out.close();
      fc.close();
      fis.close();
      return outputFile;
    }
    throw new TagNotFoundException("There is no ID3v2Tag data in this file");
  }

//  /**
//   * Returns true if this datatype contains a <code>Lyrics3</code> tag
//   * TODO disabled until Lyrics3 fixed
//   * @return true if this datatype contains a <code>Lyrics3</code> tag
//   */
//    /*
//    public boolean hasLyrics3Tag()
//    {
//        return (lyrics3tag != null);
//    }
//    */

  /**
   * Return audio header
   */
  public MP3AudioHeader getMP3AudioHeader() {
    return (MP3AudioHeader)getAudioHeader();
  }

  /**
   * Returns true if this datatype contains an <code>Id3v2</code> tag
   *
   * @return true if this datatype contains an <code>Id3v2</code> tag
   */
  public boolean hasID3v2Tag() {
    return (id3v2tag != null);
  }

  public void setID3v1Tag(Tag id3v1tag) {
    LOG.log(TRACE, "setting id3v1tag tag");
    this.id3v1tag = (ID3v1Tag)id3v1tag;
  }

//  /**
//   * Calculates hash with given algorithm. Buffer size is 32768 byte.
//   * Hash is calculated EXCLUDING meta-data, like id3v1 or id3v2
//   *
//   * @param algorithm options MD5,SHA-1,SHA-256
//   *
//   * @return hash value in byte
//   *
//   */
//
//  public byte[] getHash(String algorithm) throws NoSuchAlgorithmException, InvalidAudioFrameException, IOException {
//    return getHash(algorithm, 32768);
//  }

//  /**
//   * Calculates hash with algorithm "MD5", "SHA-1" or SHA-256".
//   * Hash is calculated EXCLUDING meta-data, like id3v1 or id3v2
//   *
//   * @return byte[] hash value in byte
//   *
//   */
//  @SuppressWarnings({"SameParameterValue", "ResultOfMethodCallIgnored"})
//  private byte[] getHash(String algorithm, int bufferSize)
//      throws InvalidAudioFrameException, IOException, NoSuchAlgorithmException {
//    File mp3File = getFile();
//    long startByte = getMP3StartByte(mp3File);
//
//    int id3v1TagSize = 0;
//    if (hasID3v1Tag()) {
//      ID3v1Tag id1tag = getID3v1Tag();
//      id3v1TagSize = id1tag.getSize();
//    }
//
//    InputStream inStream = new FileInputStream(mp3File);
//
//    byte[] buffer = new byte[bufferSize];
//
//    MessageDigest digest = MessageDigest.getInstance(algorithm);
//
//    inStream.skip(startByte);
//
//    int read;
//    long totalSize = mp3File.length() - startByte - id3v1TagSize;
//    int pointer = buffer.length;
//
//    while (pointer <= totalSize) {
//
//      read = inStream.read(buffer);
//
//      digest.update(buffer, 0, read);
//      pointer += buffer.length;
//    }
//    read = inStream.read(buffer, 0, (int)totalSize - pointer + buffer.length);
//    digest.update(buffer, 0, read);
//
//    return digest.digest();
//  }

//  /**
//   * Used by tags when writing to calculate the location of the music file
//   *
//   *  @return the location within the file that the audio starts
//   */
//  private long getMP3StartByte(File file) throws InvalidAudioFrameException, IOException {
//    try (FileChannel fileChannel = getReadFileChannel(file)) {
//      FileOperator fileOperator = new FileOperator(fileChannel);
//      //Read ID3v2 tag size (if tag exists) to allow audio header parsing to skip over tag
//      long startByte = AbstractID3v2Tag.getV2TagSizeIfExists(file);
//
//      MP3AudioHeader audioHeader = new MP3AudioHeader(fileOperator, startByte, file.getPath());
//      if (startByte != audioHeader.getMp3StartByte()) {
//        LOG.log(LogLevel.TRACE, "First header found after tag:" + audioHeader);
//        audioHeader = checkAudioStart(fileOperator, startByte, audioHeader, file.getPath());
//      }
//      return audioHeader.getMp3StartByte();
//    }
//  }

  /**
   * Returns true if this datatype contains an <code>Id3v1</code> tag
   *
   * @return true if this datatype contains an <code>Id3v1</code> tag
   */
  public boolean hasID3v1Tag() {
    return (id3v1tag != null);
  }

  /**
   * Returns the <code>ID3v1</code> tag for this dataType.
   *
   * @return the <code>ID3v1</code> tag for this dataType
   */
  public ID3v1Tag getID3v1Tag() {
    return id3v1tag;
  }

  /**
   * Sets the <code>ID3v1</code> tag for this dataType. A new
   * <code>ID3v1_1</code> dataType is created from the argument and then used
   * here.
   *
   * @param mp3tag Any MP3Tag dataType can be used and will be converted into a new ID3v1_1 dataType.
   */
  public void setID3v1Tag(BaseID3Tag mp3tag) {
    LOG.log(TRACE, "setting tagv1:abstract");
    id3v1tag = new ID3v11Tag(mp3tag);
  }

//  /**
//   * Calculates hash with given buffer size.
//   * Hash is calculated EXCLUDING meta-data, like id3v1 or id3v2
//   *
//   * @return byte[] hash value in byte
//   *
//   */
//  public byte[] getHash(int buffer) throws NoSuchAlgorithmException, InvalidAudioFrameException, IOException {
//    return getHash("MD5", buffer);
//  }

//  /**
//   * Calculates hash with algorithm "MD5". Buffer size is 32768 byte.
//   * Hash is calculated EXCLUDING meta-data, like id3v1 or id3v2
//   *
//   * @return byte[] hash value.
//   *
//   */
//  public byte[] getHash() throws NoSuchAlgorithmException, InvalidAudioFrameException, IOException {
//    return getHash("MD5", 32768);
//  }

  /**
   * Sets the <code>ID3v2</code> tag for this dataType. A new
   * <code>ID3v2_4</code> dataType is created from the argument and then used
   * here.
   *
   * @param mp3tag Any MP3Tag dataType can be used and will be converted into a new ID3v2_4 dataType.
   */
  public void setID3v2Tag(BaseID3Tag mp3tag) {
    id3v2tag = new ID3v24Tag(mp3tag);

  }

  /**
   * Set v2 tag ,don't need to set v24 tag because saving
   */
  //TODO temp its rather messy
  public void setID3v2TagOnly(AbstractID3v2Tag id3v2tag) {
    this.id3v2tag = id3v2tag;
    this.id3v2Asv24tag = null;
  }

  /**
   * Delay creating the id3V24 tag until needed. This was being created during initial read and was taking 24% of the CPU time creating
   * an V22 or V23 tag!!!! Plus, I only find this getter invoked in TEST code!
   *
   * @return a representation of tag as v24
   */
  public ID3v24Tag getID3v2TagAsv24() {
    if (id3v2Asv24tag == null) {
      if (id3v2tag instanceof ID3v24Tag) {
        id3v2Asv24tag = (ID3v24Tag)this.id3v2tag;
      } else {
        id3v2Asv24tag = new ID3v24Tag(id3v2tag);
      }
    }
    return id3v2Asv24tag;
  }

//  /**
//   * Sets the <code>Lyrics3</code> tag for this dataType. A new
//   * <code>Lyrics3v2</code> dataType is created from the argument and then
//   *
//   * used here.
//   *
//   * @param mp3tag Any MP3Tag dataType can be used and will be converted into a
//   *               new Lyrics3v2 dataType.
//   */
//    public void setLyrics3Tag(BaseID3Tag mp3tag)
//    {
//        lyrics3tag = new Lyrics3v2(mp3tag);
//    }

    /*
    public void setLyrics3Tag(AbstractLyrics3 lyrics3tag)
    {
        this.lyrics3tag = lyrics3tag;
    }
    */

//  /**
//   * Returns the <code>ID3v1</code> tag for this datatype.
//   *
//   * @return the <code>ID3v1</code> tag for this datatype
//   */
//    public AbstractLyrics3 getLyrics3Tag()
//    {
//        return lyrics3tag;
//    }

  /**
   * Remove tag from file
   */
  public void delete(BaseID3Tag mp3tag) throws IOException {
    RandomAccessFile raf = new RandomAccessFile(this.file, "rw");
    mp3tag.delete(raf);
    raf.close();
    if (mp3tag instanceof ID3v1Tag) {
      id3v1tag = null;
    }

    if (mp3tag instanceof AbstractID3v2Tag) {
      id3v2tag = null;
    }
  }

  public void save() throws CannotWriteException {
    try {
      saveMp3();
    } catch (UnableToModifyFileException umfe) {
      throw new NoWritePermissionsException(umfe);
    } catch (IOException | TagException ioe) {
      throw new CannotWriteException(ioe);
    }
  }

  /**
   * Saves the tags in this dataType to the file referred to by this dataType.
   *
   * @throws IOException  on any I/O error
   * @throws TagException on any exception generated by this library.
   */
  public void saveMp3() throws IOException, TagException {
    saveMp3(this.file);
  }

  /**
   * Saves the tags in this dataType to the file argument. It will be saved as
   * TagConstants.MP3_FILE_SAVE_WRITE
   *
   * @param fileToSave file to save the this dataTypes tags to
   *
   * @throws FileNotFoundException if unable to find file
   * @throws IOException           on any I/O error
   */
  private void saveMp3(File fileToSave) throws IOException {
    //Ensure we are dealing with absolute filepaths not relative ones
    File file = fileToSave.getAbsoluteFile();

    LOG.log(TRACE, "Saving  : %s", file);

    //Checks before starting write
    precheck(file);

    RandomAccessFile rfile = null;
    try {
      //ID3v2 Tag
      if (TagOptionSingleton.getInstance().isId3v2Save()) {
        if (id3v2tag == null) {
          rfile = new RandomAccessFile(file, "rw");
          (new ID3v24Tag()).delete(rfile);
          (new ID3v23Tag()).delete(rfile);
          (new ID3v22Tag()).delete(rfile);
          LOG.log(TRACE, "Deleting ID3v2 tag:%s", file);
          rfile.close();
        } else {
          LOG.log(TRACE, "Writing ID3v2 tag:%s", file);
          final MP3AudioHeader mp3AudioHeader = (MP3AudioHeader)this.getAudioHeader();
          final long mp3StartByte = mp3AudioHeader.getMp3StartByte();
          final long newMp3StartByte = id3v2tag.write(file, mp3StartByte);
          if (mp3StartByte != newMp3StartByte) {
            LOG.log(TRACE, "New mp3 start byte: %s", newMp3StartByte);
            mp3AudioHeader.setMp3StartByte(newMp3StartByte);
          }

        }
      }
      rfile = new RandomAccessFile(file, "rw");

      //Lyrics 3 Tag
      if (TagOptionSingleton.getInstance().isLyrics3Save()) {
        if (lyrics3tag != null) {
          lyrics3tag.write(rfile);
        }
      }
      //ID3v1 tag
      if (TagOptionSingleton.getInstance().isId3v1Save()) {
        LOG.log(TRACE, "Processing ID3v1");
        if (id3v1tag == null) {
          LOG.log(TRACE, "Deleting ID3v1");
          (new ID3v1Tag()).delete(rfile);
        } else {
          LOG.log(TRACE, "Saving ID3v1");
          id3v1tag.write(rfile);
        }
      }
    } catch (FileNotFoundException ex) {
      LOG.log(ERROR, ErrorMessage.GENERAL_WRITE_FAILED_BECAUSE_FILE_NOT_FOUND, file, ex);
      throw ex;
    } catch (IOException | RuntimeException iex) {
      LOG.log(ERROR, ErrorMessage.GENERAL_WRITE_FAILED_BECAUSE, file, iex);
      throw iex;
    } finally {
      if (rfile != null) {
        rfile.close();
      }
    }
  }

  /**
   * Check can write to file
   */
  private void precheck(File file) throws IOException {
    if (!file.exists()) {
      LOG.log(ERROR, ErrorMessage.GENERAL_WRITE_FAILED_BECAUSE_FILE_NOT_FOUND, file);
      throw new IOException(String.format(Locale.getDefault(), ErrorMessage.GENERAL_WRITE_FAILED_BECAUSE_FILE_NOT_FOUND, file.getName()));
    }

    if (TagOptionSingleton.getInstance().isCheckIsWritable() && !file.canWrite()) {
      LOG.log(ERROR, ErrorMessage.GENERAL_WRITE_FAILED, file.getName());
      throw new IOException(String.format(Locale.getDefault(), ErrorMessage.GENERAL_WRITE_FAILED, file.getName()));
    }

    if (file.length() <= MINIMUM_FILESIZE) {
      LOG.log(ERROR, ErrorMessage.GENERAL_WRITE_FAILED_BECAUSE_FILE_IS_TOO_SMALL, file.getName());
      throw new IOException(String.format(Locale.getDefault(), ErrorMessage.GENERAL_WRITE_FAILED_BECAUSE_FILE_IS_TOO_SMALL, file.getName()));
    }
  }

  /**
   * Set the Tag
   * <p>
   * If the parameter tag is a v1tag then the v1 tag is set if v2tag then the v2tag.
   */
  public Tag setTag(Tag tag) {
    this.tag = (TagFieldContainer)tag;
    if (tag instanceof ID3v1Tag) {
      setID3v1Tag((ID3v1Tag)tag);
    } else {
      setID3v2Tag((AbstractID3v2Tag)tag);
    }
    return this.tag;
  }

  /**
   * Sets the ID3v1(_1)tag to the tag provided as an argument.
   */
  public void setID3v1Tag(ID3v1Tag id3v1tag) {
    LOG.log(TRACE, "setting id3v1tag tag");
    this.id3v1tag = id3v1tag;
  }

  /**
   * Create Default Tag
   */
  @Override
  public Tag makeDefaultTag() throws UnsupportedFileType {
    return TagOptionSingleton.createDefaultID3Tag();
  }

  /**
   * Overridden to only consider ID3v2 Tag
   */
  @Override
  public Tag getTagOrSetNewDefault() throws UnsupportedFileType {
    Tag tag = getID3v2Tag();
    if (tag == null) {
      tag = setTag(makeDefaultTag());
    }
    return tag;
  }

  /**
   * Get the ID3v2 tag and convert to preferred version or if the file doesn't have one at all
   * create a default tag of preferred version and set it. The file may already contain a ID3v1 tag but because
   * this is not terribly useful the v1tag is not considered for this problem.
   */
  @Override
  public Tag getConvertedTagOrSetNewDefault() {
    return setTag(convertID3Tag((AbstractID3v2Tag)getTagOrSetNewDefault(),
                                TagOptionSingleton.getInstance().getID3V2Version()));
  }

  /**
   * Displays MP3File Structure
   */
  public String displayStructureAsXML() {
    createXMLStructureFormatter();
    tagFormatter.openHeadingElement("file", this.getFile().getAbsolutePath());
    if (this.getID3v1Tag() != null) {
      this.getID3v1Tag().createStructure();
    }
    if (this.getID3v2Tag() != null) {
      this.getID3v2Tag().createStructure();
    }
    tagFormatter.closeHeadingElement("file");
    return tagFormatter.toString();
  }

  /**
   * Displays MP3File Structure
   */
  public String displayStructureAsPlainText() {
    createPlainTextStructureFormatter();
    tagFormatter.openHeadingElement("file", this.getFile().getAbsolutePath());
    if (this.getID3v1Tag() != null) {
      this.getID3v1Tag().createStructure();
    }
    if (this.getID3v2Tag() != null) {
      this.getID3v2Tag().createStructure();
    }
    tagFormatter.closeHeadingElement("file");
    return tagFormatter.toString();
  }

  private static void createPlainTextStructureFormatter() {
    tagFormatter = new PlainTextTagDisplayFormatter();
  }

  private static void createXMLStructureFormatter() {
    tagFormatter = new XMLTagDisplayFormatter();
  }
}

