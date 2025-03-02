package com.junior.event;

import com.google.firebase.messaging.*;
import com.junior.domain.firebase.FcmNotificationToken;
import com.junior.domain.member.Member;
import com.junior.domain.story.Comment;
import com.junior.domain.story.Story;
import com.junior.repository.firebase.FcmNotificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class CommentEventListener {

    private final FcmNotificationTokenRepository fcmNotificationTokenRepository;
    private final MessageSource ms;

    private MulticastMessage getCommentMessage(Story story, List<String> tokens) {
        String message = ms.getMessage("push.comment.content", null, null);

        Notification notification = Notification.builder()
                .setTitle(story.getTitle())
                .setBody(message)
                .build();

        ApnsConfig apnsConfig = ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setSound("default")
                        .setCategory("NEW_COMMENT")
                        .setThreadId("story-thread-" + story.getId())
                        .build()

                )
                .putCustomData("storyId", String.valueOf(story.getId()))
                .build();

        return MulticastMessage.builder()
                .setNotification(notification)
                .setApnsConfig(apnsConfig)
                .addAllTokens(tokens)
                .putData("storyId", String.valueOf(story.getId()))
                .build();
    }

    @Async
    @TransactionalEventListener
    public void sendToStoryAuthor(CommentFcmEvent event) throws FirebaseMessagingException {

        Comment comment = event.getComment();
        Story story = comment.getStory();
        Member storyAuthor = story.getMember();

        List<String> tokens = fcmNotificationTokenRepository.findByMember(storyAuthor)
                .stream()
                .map(FcmNotificationToken::getToken)
                .toList();

        if (tokens.isEmpty()) {
            return;
        }

        MulticastMessage commentMessage = getCommentMessage(story, tokens);
        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(commentMessage);

        // 실팰한 토큰 디비에서 삭제
        if (response.getFailureCount() > 0) {
            List<SendResponse> responses = response.getResponses();

            IntStream.range(0, responses.size())
                    .filter(i -> !responses.get(i).isSuccessful())
                    .mapToObj(tokens::get)
                    .forEach(fcmNotificationTokenRepository::deleteAllByToken);

        }
    }
}
