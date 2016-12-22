package cn.mqclient.entity;

/**
 * Created by LinZaixiong on 2016/10/3.
 */

public class UploadData {

    /**
     * chunk : 0
     * chunks : 0
     * createBy : admin
     * createByName : 管理员
     * createTime : 2016-10-03 22:45:20
     * deleted : null
     * fileName : 5.jpg
     * id : 15c97744-2b62-4c7d-ae51-91728e31235e
     * md5 : 8c9be7cb9edc23712484c175ff78e1bd
     * path : upload/523/113/8c9be7cb9edc23712484c175ff78e1bd.jpg
     * size : 176158
     * type : image/jpeg
     * updateBy : admin
     * updateByName : 管理员
     * updateTime : 2016-10-03 22:45:20
     * version : null
     */

    private DataBean data;
    /**
     * data : {"chunk":0,"chunks":0,"createBy":"admin","createByName":"管理员","createTime":"2016-10-03 22:45:20","deleted":null,"fileName":"5.jpg","id":"15c97744-2b62-4c7d-ae51-91728e31235e","md5":"8c9be7cb9edc23712484c175ff78e1bd","path":"upload/523/113/8c9be7cb9edc23712484c175ff78e1bd.jpg","size":176158,"type":"image/jpeg","updateBy":"admin","updateByName":"管理员","updateTime":"2016-10-03 22:45:20","version":null}
     * msg : null
     * ret : 0
     */

    private String msg;
    private int ret;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public static class DataBean {
        private int chunk;
        private int chunks;
        private String createBy;
        private String createByName;
        private String createTime;
        private String fileName;
        private String id;
        private String md5;
        private String path;
        private int size;
        private String type;
        private String updateBy;
        private String updateByName;
        private String updateTime;

        public int getChunk() {
            return chunk;
        }

        public void setChunk(int chunk) {
            this.chunk = chunk;
        }

        public int getChunks() {
            return chunks;
        }

        public void setChunks(int chunks) {
            this.chunks = chunks;
        }

        public String getCreateBy() {
            return createBy;
        }

        public void setCreateBy(String createBy) {
            this.createBy = createBy;
        }

        public String getCreateByName() {
            return createByName;
        }

        public void setCreateByName(String createByName) {
            this.createByName = createByName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }


        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUpdateBy() {
            return updateBy;
        }

        public void setUpdateBy(String updateBy) {
            this.updateBy = updateBy;
        }

        public String getUpdateByName() {
            return updateByName;
        }

        public void setUpdateByName(String updateByName) {
            this.updateByName = updateByName;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

    }
}
