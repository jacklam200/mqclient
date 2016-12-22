package cn.mqclient.service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.mqclient.entity.FolderInfo;
import cn.mqclient.entity.MusicInfo;
import cn.mqclient.service.db.FolderInfoDao;
import cn.mqclient.service.db.MusicInfoDao;
import cn.mqclient.service.db.SPStorage;

import static cn.mqclient.service.MusicConstants.START_FROM_ARTIST;
import static cn.mqclient.service.MusicConstants.START_FROM_FOLDER;
import static cn.mqclient.service.MusicConstants.START_FROM_LOCAL;

/**
 * Created by LinZaixiong on 2016/10/15.
 */

public class MusicUtils {
    private static String[] proj_music = new String[] {
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION };

    public static final int FILTER_SIZE = 1 * 1024 * 1024;// 1MB
    public static final int FILTER_DURATION = 1 * 60 * 1000;// 1分钟

    private static String[] proj_folder = new String[] { MediaStore.Files.FileColumns.DATA };
    // 文件夹信息数据库
    private static FolderInfoDao mFolderInfoDao;
    // 歌曲信息数据库
    private static MusicInfoDao mMusicInfoDao;

    /**
     * 获取包含音频文件的文件夹信息
     * @param context
     * @return
     */
    public static List<FolderInfo> queryFolder(Context context) {
        if(mFolderInfoDao == null) {
            mFolderInfoDao = new FolderInfoDao(context);
        }
        SPStorage sp = new SPStorage(context);
        Uri uri = MediaStore.Files.getContentUri("external");
        ContentResolver cr = context.getContentResolver();
        StringBuilder mSelection = new StringBuilder(MediaStore.Files.FileColumns.MEDIA_TYPE
                + " = " + MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO + " and " + "("
                + MediaStore.Files.FileColumns.DATA + " like'%.mp3' or " + MediaStore.Audio.Media.DATA
                + " like'%.wma')");
        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        if(sp.getFilterSize()) {
            mSelection.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        }
        if(sp.getFilterTime()) {
            mSelection.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
        }
        mSelection.append(") group by ( " + MediaStore.Files.FileColumns.PARENT);
        if (mFolderInfoDao.hasData()) {
            return mFolderInfoDao.getFolderInfo();
        } else {
            List<FolderInfo> list = getFolderList(cr.query(uri, proj_folder, mSelection.toString(), null, null));
            mFolderInfoDao.saveFolderInfo(list);
            return list;
        }
    }

    /**
     *
     * @param context
     * @param from 不同的界面进来要做不同的查询
     * @return
     */
    public static List<MusicInfo> queryMusic(Context context, int from) {
        return queryMusic(context, null, null, from);
    }

    public static List<MusicInfo> queryMusic(Context context,
                                             String selections, String selection, int from) {
        if(mMusicInfoDao == null) {
            mMusicInfoDao = new MusicInfoDao(context);
        }
        SPStorage sp = new SPStorage(context);
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();

        StringBuffer select = new StringBuffer(" 1=1 ");
        // 查询语句：检索出.mp3为后缀名，时长大于1分钟，文件大小大于1MB的媒体文件
        if(sp.getFilterSize()) {
            select.append(" and " + MediaStore.Audio.Media.SIZE + " > " + FILTER_SIZE);
        }
        if(sp.getFilterTime()) {
            select.append(" and " + MediaStore.Audio.Media.DURATION + " > " + FILTER_DURATION);
        }

        if (!TextUtils.isEmpty(selections)) {
            select.append(selections);
        }

        switch(from) {
            case START_FROM_LOCAL:
                if (mMusicInfoDao.hasData()) {
                    return mMusicInfoDao.getMusicInfo();
                } else {
                    List<MusicInfo> list = getMusicList(cr.query(uri, proj_music,
                            select.toString(), null,
                            MediaStore.Audio.Media.ARTIST_KEY));
                    mMusicInfoDao.saveMusicInfo(list);
                    return list;
                }

            case START_FROM_FOLDER:
                if(mMusicInfoDao.hasData()) {
                    return mMusicInfoDao.getMusicInfoByType(selection, START_FROM_FOLDER);
                }
            default:
                return null;
        }

    }


    public static ArrayList<MusicInfo> getMusicList(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        ArrayList<MusicInfo> musicList = new ArrayList<MusicInfo>();
        while (cursor.moveToNext()) {
            MusicInfo music = new MusicInfo();
            music.songId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            music.albumId = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            music.duration = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));
            music.musicName = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE));
            music.artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));

            String filePath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            music.data = filePath;
            String folderPath = filePath.substring(0,
                    filePath.lastIndexOf(File.separator));
            music.folder = folderPath;
            music.musicNameKey = StringHelper.getPingYin(music.musicName);
            music.artistKey = StringHelper.getPingYin(music.artist);
            musicList.add(music);
        }
        cursor.close();
        return musicList;
    }

    public static List<FolderInfo> getFolderList(Cursor cursor) {
        List<FolderInfo> list = new ArrayList<FolderInfo>();
        while (cursor.moveToNext()) {
            FolderInfo info = new FolderInfo();
            String filePath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Files.FileColumns.DATA));
            info.folder_path = filePath.substring(0,
                    filePath.lastIndexOf(File.separator));
            info.folder_name = info.folder_path.substring(info.folder_path
                    .lastIndexOf(File.separator) + 1);
            list.add(info);
        }
        cursor.close();
        return list;
    }

    public static String makeTimeString(long milliSecs) {
        StringBuffer sb = new StringBuffer();
        long m = milliSecs / (60 * 1000);
        sb.append(m < 10 ? "0" + m : m);
        sb.append(":");
        long s = (milliSecs % (60 * 1000)) / 1000;
        sb.append(s < 10 ? "0" + s : s);
        return sb.toString();
    }

    /**
     * 根据歌曲的ID，寻找出歌曲在当前播放列表中的位置
     *
     * @param list
     * @param id
     * @return
     */
    public static int seekPosInListById(List<MusicInfo> list, int id) {
        if(id == -1) {
            return -1;
        }
        int result = -1;
        if (list != null) {

            for (int i = 0; i < list.size(); i++) {
                if (id == list.get(i).songId) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }



}
