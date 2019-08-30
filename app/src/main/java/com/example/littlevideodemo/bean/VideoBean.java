package com.example.littlevideodemo.bean;

/**
 * @author weioule
 * @date 2019/8/3.
 */
public class VideoBean {

    private Data data;
    private String type;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class Data {
        private Content content;
        private String type;

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Content {
        private ContentData data;
        private String type;

        public ContentData getData() {
            return data;
        }

        public void setData(ContentData data) {
            this.data = data;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class ContentData {
        private String playUrl;
        private String title;
        private Cover cover;
        private Author author;
        private Consumption consumption;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Author getAuthor() {
            return author;
        }

        public void setAuthor(Author author) {
            this.author = author;
        }

        public String getPlayUrl() {
            return playUrl;
        }

        public void setPlayUrl(String playUrl) {
            this.playUrl = playUrl;
        }

        public Cover getCover() {
            return cover;
        }

        public void setCover(Cover cover) {
            this.cover = cover;
        }

        public Consumption getConsumption() {
            return consumption;
        }

        public void setConsumption(Consumption consumption) {
            this.consumption = consumption;
        }
    }

    public static class Cover {
        private String detail;
        private String blurred;

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }

        public String getBlurred() {
            return blurred;
        }

        public void setBlurred(String blurred) {
            this.blurred = blurred;
        }
    }

    public static class Author {
        private String icon;
        private String description;
        private String name;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Consumption {
        private int shareCount;
        private int replyCount;
        private int collectionCount;
        private boolean has_favour;

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public boolean isHas_favour() {
            return has_favour;
        }

        public void setHas_favour(boolean has_favour) {
            this.has_favour = has_favour;
        }

        public int getShareCount() {
            return shareCount;
        }

        public void setShareCount(int shareCount) {
            this.shareCount = shareCount;
        }

        public int getCollectionCount() {
            return collectionCount;
        }

        public void setCollectionCount(int collectionCount) {
            this.collectionCount = collectionCount;
        }
    }
}
