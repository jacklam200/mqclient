package cn.mqclient.entity.http;

/**
 * Created by LinZaixiong on 2016/10/18.
 */

public class TokenEntity extends BaseEntity{

    private Token data;

    public Token getData(){
        return data;
    }

    public void setData(Token data){
        this.data = data;
    }
    /**
     * companyId : 1
     * endTime : 1474646400000
     * startTime : 1472711877000
     * token : a40517726a0e3aae2b3d01d35e37b621cd6630b9fbea5b7ac5c045160192489a6b5873a4e739d94f93d879d59633afc00caaf5b81d205a92afd8f0018ecf0820359aa8e8aa47fd71334c15f1e550fd81fabe1d5ada32874524aded32726a7b1f3a4c91aba13598b0a6bc5687d8cac7843d8078a2e6fb8da7cad22c49edf6d39185f6b0ffbaab37a62e3e31bae7a555c98dd23256dd749acfd477230e0588c9ac0b7fd18800055358b89e1a195f64dbbae3d471ba60b66d20f62dc8e0fc9cce3ef38de3de035a5042a00e3687276abbfa
     * tokenExpires : 1476775845080
     * userId : 3cb0814b-92d1-11e6-8a45-525400b6adff
     * warrantNo : 945a555c-0eba-4068-963a-baf2d8ecda6d
     */

    public static class Token{

        private String companyId;
        private long endTime;
        private long startTime;
        private String token;
        private long tokenExpires;
        private String userId;
        private String warrantNo;
        private String counting;

        public String getCounting() {
            return counting;
        }

        public void setCounting(String counting) {
            this.counting = counting;
        }

        public String getCompanyId() {
            return companyId;
        }

        public void setCompanyId(String companyId) {
            this.companyId = companyId;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getTokenExpires() {
            return tokenExpires;
        }

        public void setTokenExpires(long tokenExpires) {
            this.tokenExpires = tokenExpires;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getWarrantNo() {
            return warrantNo;
        }

        public void setWarrantNo(String warrantNo) {
            this.warrantNo = warrantNo;
        }
    }

}
