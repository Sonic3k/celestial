-- ============================================================
-- V2 — Seed: Rider-Waite-Smith deck + 6 Major Arcana cards
-- ============================================================

INSERT INTO decks (module, name_vi, name_en, description, card_count, style)
VALUES (
    'tarot',
    'Rider-Waite-Smith',
    'Rider-Waite-Smith',
    'Bộ bài Tarot cổ điển nhất, được vẽ bởi Pamela Colman Smith dưới sự chỉ đạo của Arthur Edward Waite (1909). Đây là nền tảng của hầu hết các bộ bài hiện đại.',
    78,
    'classic'
);

-- Lưu deck_id vào biến để dùng cho cards
DO $$
DECLARE
    v_deck_id BIGINT;
BEGIN
    SELECT id INTO v_deck_id FROM decks WHERE name_en = 'Rider-Waite-Smith' LIMIT 1;

    -- 0. The Fool
    INSERT INTO cards (deck_id, card_index, name_vi, name_en, arcana, number,
                       keywords_upright, keywords_reversed,
                       meaning_upright, meaning_reversed,
                       description, element, planet_or_sign, numerology_link)
    VALUES (v_deck_id, 0, 'Kẻ Điên', 'The Fool', 'major', 0,
            'khởi đầu mới, tự do, tinh thần phiêu lưu, tiềm năng vô hạn',
            'thiếu thận trọng, liều lĩnh, bất cẩn, từ chối trưởng thành',
            'The Fool báo hiệu một khởi đầu hoàn toàn mới — một bước nhảy vào điều chưa biết với trái tim rộng mở. Đây là lúc để tin tưởng vào vũ trụ, buông bỏ nỗi sợ hãi và đón nhận cuộc hành trình phía trước dù chưa biết đích đến.',
            'Bạn đang do dự quá mức hoặc hành động quá liều lĩnh mà không suy nghĩ. Có thể bạn đang trốn tránh trách nhiệm, hoặc ngược lại — đang lao đầu vào nguy hiểm mà không chuẩn bị.',
            'Một chàng trai trẻ đứng ở mép vực, nhìn lên bầu trời, tay cầm gậy với túi nhỏ, bên cạnh là chú chó trắng. Núi non trắng tuyết phía sau tượng trưng cho đỉnh cao chưa đạt được.',
            'Air', 'Uranus', 0);

    -- 1. The Magician
    INSERT INTO cards (deck_id, card_index, name_vi, name_en, arcana, number,
                       keywords_upright, keywords_reversed,
                       meaning_upright, meaning_reversed,
                       description, element, planet_or_sign, numerology_link)
    VALUES (v_deck_id, 1, 'Nhà Ảo Thuật', 'The Magician', 'major', 1,
            'ý chí, kỹ năng, tập trung, biểu hiện, sức mạnh cá nhân',
            'thao túng, lừa dối, kỹ năng chưa phát triển, lãng phí tiềm năng',
            'The Magician là lá bài của sức mạnh và ý chí. Bạn có đủ công cụ, kỹ năng và nguồn lực để biến ý tưởng thành hiện thực. Đây là thời điểm để hành động có chủ đích — tập trung năng lượng vào một mục tiêu rõ ràng.',
            'Bạn đang không tận dụng hết tiềm năng của mình, hoặc đang dùng kỹ năng vào những mục đích không lành mạnh. Cũng có thể có người đang thao túng bạn.',
            'Một người đàn ông đứng trước bàn với đầy đủ 4 biểu tượng của 4 chất bài. Một tay chỉ lên trời, một tay chỉ xuống đất — "As above, so below". Vòng lemniscate (∞) trên đầu.',
            'Air', 'Mercury', 1);

    -- 2. The High Priestess
    INSERT INTO cards (deck_id, card_index, name_vi, name_en, arcana, number,
                       keywords_upright, keywords_reversed,
                       meaning_upright, meaning_reversed,
                       description, element, planet_or_sign, numerology_link)
    VALUES (v_deck_id, 2, 'Nữ Tư Tế', 'The High Priestess', 'major', 2,
            'trực giác, tiềm thức, bí ẩn, kiến thức bên trong, sự thụ động',
            'bí mật bị che giấu, rút lui, ngắt kết nối với trực giác',
            'The High Priestess kêu gọi bạn lắng nghe tiếng nói bên trong. Câu trả lời bạn tìm kiếm không nằm ở thế giới bên ngoài — hãy thiền định, tin vào trực giác và kiên nhẫn chờ đợi sự rõ ràng xuất hiện.',
            'Bạn đang phớt lờ trực giác hoặc có điều gì đó đang bị che giấu. Cũng có thể bạn đang rút vào trong quá mức, cô lập bản thân khỏi thế giới.',
            'Một phụ nữ ngồi giữa hai cây cột đen trắng (B và J), trước tấm màn thêu lựu — biểu tượng của ranh giới giữa ý thức và tiềm thức. Trăng lưỡi liềm dưới chân.',
            'Water', 'Moon', 2);

    -- 17. The Star
    INSERT INTO cards (deck_id, card_index, name_vi, name_en, arcana, number,
                       keywords_upright, keywords_reversed,
                       meaning_upright, meaning_reversed,
                       description, element, planet_or_sign, numerology_link)
    VALUES (v_deck_id, 17, 'Ngôi Sao', 'The Star', 'major', 17,
            'hy vọng, đổi mới, bình yên, cảm hứng, niềm tin vào tương lai',
            'tuyệt vọng, mất niềm tin, thiếu tự tin, ngắt kết nối',
            'Sau những thử thách, The Star mang đến ánh sáng hy vọng. Đây là lúc để chữa lành, để tin rằng mọi thứ sẽ tốt hơn. Vũ trụ đang ủng hộ bạn — hãy mở lòng đón nhận sự đổi mới và bình an.',
            'Bạn đang mất đi niềm tin vào bản thân hoặc vào tương lai. Có thể bạn đang bi quan, thất vọng, hoặc không nhìn thấy ánh sáng cuối đường hầm.',
            'Một phụ nữ khỏa thân quỳ bên hồ nước, rót nước từ hai bình — một xuống đất, một xuống hồ. Trên đầu là 8 ngôi sao, ngôi sao lớn nhất ở trung tâm có 8 cánh.',
            'Air', 'Aquarius', 8);

    -- 18. The Moon
    INSERT INTO cards (deck_id, card_index, name_vi, name_en, arcana, number,
                       keywords_upright, keywords_reversed,
                       meaning_upright, meaning_reversed,
                       description, element, planet_or_sign, numerology_link)
    VALUES (v_deck_id, 18, 'Mặt Trăng', 'The Moon', 'major', 18,
            'ảo giác, nỗi sợ, tiềm thức, giấc mơ, sự không chắc chắn',
            'sự rõ ràng trở lại, giải phóng nỗi sợ, vượt qua ảo giác',
            'The Moon cảnh báo rằng mọi thứ không phải như vẻ bề ngoài. Nỗi sợ hãi và ảo giác đang chi phối nhận thức của bạn. Hãy tin vào trực giác nhưng cẩn thận với những ảo tưởng do tâm trí tạo ra.',
            'Sương mù đang tan dần — sự thật bắt đầu lộ rõ. Những nỗi sợ bạn đang mang theo có thể không có cơ sở thực tế. Đây là lúc để đối mặt và giải phóng chúng.',
            'Mặt trăng toả ánh sáng mờ ảo xuống một con đường dẫn vào bóng tối xa xa. Hai con chó sói hú vào trăng, một con cua bò lên từ hồ. Hai tháp canh đứng hai bên đường.',
            'Water', 'Pisces', 9);

    -- 20. Judgement
    INSERT INTO cards (deck_id, card_index, name_vi, name_en, arcana, number,
                       keywords_upright, keywords_reversed,
                       meaning_upright, meaning_reversed,
                       description, element, planet_or_sign, numerology_link)
    VALUES (v_deck_id, 20, 'Phán Xét', 'Judgement', 'major', 20,
            'sự thức tỉnh, phán xét, tha thứ, tái sinh, lời kêu gọi',
            'tự phán xét khắc nghiệt, nghi ngờ bản thân, bỏ lỡ cơ hội',
            'Judgement kêu gọi bạn nhìn lại toàn bộ hành trình đã qua với con mắt tha thứ và chấp nhận. Đây là thời điểm để thức tỉnh, để đáp lại lời kêu gọi của số phận và bước vào một giai đoạn mới với tâm thế được thanh lọc.',
            'Bạn đang tự phán xét bản thân quá khắc nghiệt, hoặc đang phủ nhận cơ hội thay đổi. Hãy tha thứ cho chính mình và tiến về phía trước.',
            'Thiên thần Gabriel thổi kèn từ trên mây, những người từ quan tài vươn tay lên trời. Hình ảnh tái sinh — sự kết thúc của một chu kỳ và bắt đầu của điều gì đó lớn lao hơn.',
            'Fire', 'Pluto', 2);

END $$;