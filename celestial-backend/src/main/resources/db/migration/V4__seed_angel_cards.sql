-- ============================================================
-- V4 — Seed: Angel Cards deck + 6 sample oracle cards
-- ============================================================

INSERT INTO oracle_decks (name_vi, name_en, description, card_count, style)
VALUES (
    'Angel Cards',
    'Angel Cards',
    'Những thông điệp từ các thiên thần hộ mệnh — nhẹ nhàng, chữa lành, và đầy tình yêu thương. Mỗi lá bài là một lời nhắc nhở về sức mạnh và ánh sáng bên trong bạn.',
    44,
    'angelic'
);

DO $$
DECLARE
    v_deck_id BIGINT;
BEGIN
    SELECT id INTO v_deck_id FROM oracle_decks WHERE name_en = 'Angel Cards' LIMIT 1;

    INSERT INTO oracle_cards (deck_id, card_index, name_vi, name_en, keywords, message, affirmation, description, element)
    VALUES
    (v_deck_id, 1, 'Chữa Lành', 'Healing',
     'phục hồi, chữa lành, bình yên, tái sinh',
     'Đây là thời điểm để chữa lành — cả thể xác lẫn tâm hồn. Hãy cho phép bản thân nghỉ ngơi, nhận sự giúp đỡ, và tin rằng mọi vết thương đều có thể lành lại. Các thiên thần đang bao bọc bạn trong ánh sáng chữa lành.',
     'Tôi cho phép bản thân được chữa lành hoàn toàn.',
     'Một vòng ánh sáng vàng ấm áp tỏa ra từ trung tâm, bao quanh bởi những cánh hoa trắng tinh khiết.',
     'Water'),

    (v_deck_id, 2, 'Tin Tưởng', 'Trust',
     'niềm tin, buông bỏ, bình an, phó thác',
     'Vũ trụ đang sắp xếp mọi thứ theo đúng kế hoạch hoàn hảo — dù bạn chưa thấy toàn bộ bức tranh. Hãy buông bỏ nỗi lo âu và tin tưởng rằng mọi thứ đang diễn ra vì lợi ích tốt nhất của bạn.',
     'Tôi tin tưởng vào tiến trình của cuộc đời.',
     'Một đôi bàn tay mở ra hướng lên trời, ánh sáng trắng rót xuống từ phía trên.',
     'Air'),

    (v_deck_id, 3, 'Tình Yêu', 'Love',
     'tình yêu, kết nối, lòng trắc ẩn, mở lòng',
     'Tình yêu đang hiện diện xung quanh bạn — trong những mối quan hệ, trong thiên nhiên, và quan trọng nhất là trong trái tim bạn. Hãy mở lòng đón nhận tình yêu và cho đi tình yêu một cách tự do.',
     'Tôi xứng đáng được yêu thương và tôi yêu thương hoàn toàn.',
     'Trái tim màu hồng phát sáng, được bao quanh bởi những cánh bướm và bông hoa nhỏ.',
     'Fire'),

    (v_deck_id, 4, 'Dũng Cảm', 'Courage',
     'dũng cảm, bước tiếp, vượt qua sợ hãi, sức mạnh',
     'Các thiên thần thấy sức mạnh trong bạn — dù bạn chưa nhìn thấy nó. Đây là lúc để bước ra khỏi vùng an toàn, đối mặt với nỗi sợ hãi, và tiến về phía trước. Bạn không đơn độc trong hành trình này.',
     'Tôi có đủ dũng cảm để sống đúng với bản thân.',
     'Một ngọn lửa vàng cháy sáng giữa bóng tối, không bị dập tắt bởi gió.',
     'Fire'),

    (v_deck_id, 5, 'Phong Phú', 'Abundance',
     'dồi dào, biết ơn, thịnh vượng, nhận lãnh',
     'Vũ trụ luôn có đủ cho tất cả. Hãy mở rộng tâm trí để nhận ra sự dồi dào đang hiện diện trong cuộc sống của bạn — không chỉ về vật chất mà còn về tình yêu, sức khỏe, và niềm vui.',
     'Tôi mở lòng đón nhận mọi điều tốt đẹp từ vũ trụ.',
     'Những đồng xu vàng và hoa quả chín rộ đổ xuống từ một chiếc sừng đầy ắp.',
     'Earth'),

    (v_deck_id, 6, 'Chuyển Hóa', 'Transformation',
     'thay đổi, biến đổi, tái sinh, buông bỏ cũ',
     'Một sự chuyển hóa sâu sắc đang diễn ra trong cuộc đời bạn. Hãy chào đón nó thay vì chống lại. Như con bướm rời khỏi kén, bạn đang trở thành phiên bản tốt hơn của chính mình.',
     'Tôi chào đón sự thay đổi như một cơ hội để phát triển.',
     'Một con bướm đang thoát ra khỏi kén, cánh còn ướt nhưng rực rỡ màu sắc.',
     'Air');

END $$;
