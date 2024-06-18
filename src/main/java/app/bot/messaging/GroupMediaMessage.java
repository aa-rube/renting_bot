package app.bot.messaging;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

import java.util.ArrayList;
import java.util.List;

public class GroupMediaMessage {

    public static Object getMediaGroupMessage(Long chatId, MediaGroupData data, String caption) {
        List<InputMedia> media = getInputMedia(data);

        try {
            media.get(0).setCaption(caption);
            media.get(0).setParseMode(ParseMode.HTML);

            return TelegramData.getSendMediaGroupMsg(chatId, media);
        } catch (Exception e) {
            e.printStackTrace();

            media.get(0).setCaption(caption);

            return TelegramData.getSendMediaGroupMsg(chatId, media);
        }
    }

    private static List<InputMedia> getInputMedia(MediaGroupData data) {
        List<InputMedia> media = new ArrayList<>();

        if (!data.listPhotoFilesId.isEmpty()) {

            for (java.io.File javaIoFile : data.getListPhotoFilesId()) {
                InputMedia inputMedia = new InputMediaPhoto();
                inputMedia.setMedia(javaIoFile, javaIoFile.getName());
                media.add(inputMedia);

            }
        }

        if (!data.getListVideoFilesId().isEmpty()) {

            for (java.io.File javaIoFile : data.getListVideoFilesId()) {
                InputMedia inputMedia = new InputMediaVideo();
                inputMedia.setMedia(javaIoFile, javaIoFile.getName());
                media.add(inputMedia);
            }
        }

        return media;
    }

    @Getter
    @Setter
    public static class MediaGroupData {
        private List<java.io.File> listPhotoFilesId = new ArrayList<>();
        private List<java.io.File> listVideoFilesId = new ArrayList<>();
    }
}
