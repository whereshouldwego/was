@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String CHAT_QUEUE = "chat.queue";

    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public void sendChatMessageToQueue(ChatMessageRequestDto dto) {
        rabbitTemplate.convertAndSend(CHAT_QUEUE, dto);
    }

    public List<ChatMessageResponseDto> getChatHistory(String roomCode) {
        return chatMessageRepository.findByRoomCodeOrderByCreatedAtAsc(roomCode)
                .stream()
                .map(msg -> ChatMessageResponseDto.builder()
                        .id(msg.getId() != null ? msg.getId().toHexString() : null)
                        .roomCode(msg.getRoomCode())
                        .userId(msg.getUserId())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
