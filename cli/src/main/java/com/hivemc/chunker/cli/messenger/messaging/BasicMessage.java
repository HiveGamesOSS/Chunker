package com.hivemc.chunker.cli.messenger.messaging;

import com.google.gson.annotations.SerializedName;
import com.hivemc.chunker.cli.messenger.messaging.request.*;
import com.hivemc.chunker.cli.messenger.messaging.response.ErrorResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.OutputResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.ProgressResponse;
import com.hivemc.chunker.cli.messenger.messaging.response.ProgressStateResponse;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.UUID;

/**
 * BasicMessage is a message that can be sent between Backend and Chunker.
 */
public abstract class BasicMessage {
    private final BasicMessageType type;
    private final UUID requestId;

    /**
     * Create a new basic message with random request ID.
     */
    public BasicMessage() {
        type = BasicMessageType.CLASS_TO_MESSAGE.get(getClass());
        requestId = UUID.randomUUID();
    }

    /**
     * Create a new basic message with a specific ID.
     *
     * @param requestId the specific request ID.
     */
    public BasicMessage(UUID requestId) {
        type = BasicMessageType.CLASS_TO_MESSAGE.get(getClass());
        this.requestId = requestId;
    }

    /**
     * Get the type which is used for this message.
     *
     * @return the type used for serializing.
     */
    public BasicMessageType getType() {
        return type;
    }

    /**
     * Get the request ID which for this message, used for responding.
     *
     * @return the UUID for the request.
     */
    public UUID getRequestId() {
        return requestId;
    }

    /**
     * An enum of all the different message types.
     */
    public enum BasicMessageType {
        // Input
        @SerializedName("detect_version")
        DETECT_VERSION(DetectVersionRequest.class),
        @SerializedName("settings")
        SETTINGS(SettingsRequest.class),
        @SerializedName("preview")
        PREVIEW(PreviewRequest.class),
        @SerializedName("convert")
        CONVERT(ConvertRequest.class),
        @SerializedName("kill")
        KILL(KillRequest.class),

        // Output
        @SerializedName("response")
        RESPONSE(OutputResponse.class),
        @SerializedName("progress")
        PROGRESS(ProgressResponse.class),
        @SerializedName("progress_state")
        PROGRESS_STATE(ProgressStateResponse.class),
        @SerializedName("error")
        ERROR(ErrorResponse.class);

        private static final Map<Class<? extends BasicMessage>, BasicMessageType> CLASS_TO_MESSAGE = new Object2ObjectOpenHashMap<>();

        static {
            for (BasicMessageType type : values()) {
                CLASS_TO_MESSAGE.put(type.getMessageClass(), type);
            }
        }

        private final Class<? extends BasicMessage> messageClass;

        /**
         * Create a new message type.
         *
         * @param messageClass the class which is constructed when the type is found.
         */
        BasicMessageType(Class<? extends BasicMessage> messageClass) {
            this.messageClass = messageClass;
        }

        /**
         * Get the class which should be used based on the type.
         *
         * @return the class extending basic message.
         */
        public Class<? extends BasicMessage> getMessageClass() {
            return messageClass;
        }
    }
}
