//package coursework.poterashki.controller;
//
//import coursework.poterashki.controller.dto.request.RequestDataAnnouncement;
//import coursework.poterashki.controller.dto.response.ResponseAnnouncement;
//import coursework.poterashki.controller.dto.response.ResponseProfile;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@RestController
//@RequestMapping("/announcements")
//public class AnnouncementController {
//
//    // Временное хранилище (в реальном приложении будет БД)
//    private final Map<String, ResponseAnnouncement> announcements = new HashMap<>();
//
//    @PostMapping
//    public ResponseEntity<?> createAnnouncement(
//            @RequestBody RequestDataAnnouncement data
//    ) {
//        System.out.println("📝 Создание нового объявления:");
//        System.out.println("📌 Заголовок: " + data.getTitle());
//        System.out.println("🏷️ Категория: " + data.getCategory());
//        System.out.println("📋 Описание: " + data.getDescription());
//        System.out.println("🏙️ Город: " + data.getCity());
//        System.out.println("📍 Адрес: " + data.getAddress());
//        System.out.println("📅 Дата потери: " + data.getLostDate());
//        System.out.println("🎨 Цвет: " + data.getColor());
//        System.out.println("💰 Вознаграждение: " + data.getReward());
//        System.out.println("👤 ID пользователя: " + data.getUserId());
//        System.out.println("✏️ создание объявления с found: " + data.getIsFound());
//
//
//        // Валидация
//        if (data.getTitle() == null || data.getTitle().trim().isEmpty()) {
//            return ResponseEntity.badRequest().body("Title is required");
//        }
//        if (data.getCity() == null || data.getCity().trim().isEmpty()) {
//            return ResponseEntity.badRequest().body("City is required");
//        }
//        if (data.getLostDate() == null) {
//            return ResponseEntity.badRequest().body("Lost date is required");
//        }
//
//        // Создаем ответ
//        ResponseAnnouncement response = new ResponseAnnouncement();
//        String id = UUID.randomUUID().toString();
//        response.setId(id);
//        response.setTitle(data.getTitle());
//        response.setCategory(data.getCategory());
//        response.setDescription(data.getDescription());
//        response.setCity(data.getCity());
//        response.setAddress(data.getAddress());
//        response.setLostDate(data.getLostDate());
//        response.setColor(data.getColor());
//        response.setReward(data.getReward() != null ? data.getReward() : 0);
//        response.setUserId(data.getUserId());
//        response.setUserName(data.getUserName());
//        response.setCreatedAt(LocalDateTime.now());
//        response.setUpdatedAt(LocalDateTime.now());
//        response.setIsActive(true);
//
//        ArrayList<ResponseProfile> responses = new ArrayList<ResponseProfile>();
//        ResponseProfile responseProfile11 = new ResponseProfile();
//        responseProfile11.setId("125");
//        responseProfile11.setName("Петр");
//        responseProfile11.setPhone("+8 900 555 35 35");
//        responseProfile11.setEmail("test@mail.ru");
//        responses.add(responseProfile11);
//        ResponseProfile responseProfile22 = new ResponseProfile();
//        responseProfile22.setId("124");
//        responseProfile22.setName("Олег");
//        responseProfile22.setPhone("+8 900 555 35 36");
//        responseProfile22.setEmail("test2@mail.ru");
//        responses.add(responseProfile22);
//        response.setRespondedUsers(responses);
//        response.setIsFound(data.getIsFound());
//
//        // Сохраняем фото (в реальном приложении нужно сохранять файл)
////        if (photo != null && !photo.isEmpty()) {
////            String photoUrl = "/uploads/" + id + "_" + photo.getOriginalFilename();
////            response.setPhotoUrl(photoUrl);
////            System.out.println("🖼️ Фото сохранено: " + photoUrl);
////        }
//
//        // Сохраняем в хранилище
//        announcements.put(id, response);
//
//
//
//        ResponseAnnouncement response2 = new ResponseAnnouncement();
//        ArrayList<ResponseProfile> responses2 = new ArrayList<ResponseProfile>();
//        ResponseProfile responseProfile2 = new ResponseProfile();
//        responseProfile2.setId("125");
//        responseProfile2.setName("Петр");
//        responseProfile2.setPhone("+8 900 555 35 35");
//        responseProfile2.setEmail("test@mail.ru");
//        responses2.add(responseProfile2);
//
//        responses2.add(responseProfile2);
//        response2.setId(UUID.randomUUID().toString());
//        response2.setTitle("Телефон");
//        response2.setCategory(data.getCategory());
//        response2.setDescription("Трали вали");
//        response2.setCity("Омск");
//        response2.setAddress(data.getAddress());
//        response2.setLostDate(data.getLostDate());
//        response2.setColor(data.getColor());
//        response2.setReward(data.getReward() != null ? data.getReward() : 0);
//        response2.setUserId("124");
//        response2.setUserName("Олег");
//        response2.setCreatedAt(LocalDateTime.now());
//        response2.setUpdatedAt(LocalDateTime.now());
//        response2.setIsActive(true);
//        response2.setRespondedUsers(responses2);
//        response2.setIsFound(true);
//        announcements.put(response2.getId(), response2);
//
//        ResponseAnnouncement response3 = new ResponseAnnouncement();
//        ArrayList<ResponseProfile> responses3 = new ArrayList<ResponseProfile>();
//        ResponseProfile responseProfile3 = new ResponseProfile();
//        responseProfile3.setId("123");
//        responseProfile3.setName("Петр");
//        responseProfile3.setPhone("+8 900 555 35 35");
//        responseProfile3.setEmail("test@mail.ru");
//        responses3.add(responseProfile3);
//
//        response3.setId(UUID.randomUUID().toString());
//        response3.setTitle("Ноутбук");
//        response3.setCategory(data.getCategory());
//        response3.setDescription("Трали вали 2");
//        response3.setCity("Томск");
//        response3.setAddress(data.getAddress());
//        response3.setLostDate(data.getLostDate());
//        response3.setColor(data.getColor());
//        response3.setReward(data.getReward() != null ? data.getReward() : 0);
//        response3.setUserId("125");
//        response3.setUserName("Петр");
//        response3.setCreatedAt(LocalDateTime.now());
//        response3.setUpdatedAt(LocalDateTime.now());
//        response3.setIsActive(true);
//        response3.setRespondedUsers(responses3);
//        response3.setIsFound(false);
//        announcements.put(response3.getId(), response3);
//
//        System.out.println("✅ Объявление создано с ID: " + id);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getAnnouncement(@PathVariable String id) {
//        System.out.println("📥 Запрос объявления с ID: " + id);
//
//        ResponseAnnouncement announcement = announcements.get(id);
//        if (announcement == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        System.out.println("✅ Объявление найдено: " + announcement.getTitle());
//        return ResponseEntity.ok(announcement);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateAnnouncement(
//            @PathVariable String id,
//            @RequestBody RequestDataAnnouncement data
//    ) {
//        System.out.println("✏️ Обновление объявления с ID: " + id);
//        System.out.println("✏️ Обновление объявления с found: " + data.getIsFound());
//
//        ResponseAnnouncement announcement = announcements.get(id);
//        if (announcement == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // Обновляем поля
//        announcement.setTitle(data.getTitle());
//        announcement.setCategory(data.getCategory());
//        announcement.setDescription(data.getDescription());
//        announcement.setCity(data.getCity());
//        announcement.setAddress(data.getAddress());
//        announcement.setLostDate(data.getLostDate());
//        announcement.setColor(data.getColor());
//        announcement.setReward(data.getReward() != null ? data.getReward() : 0);
//        announcement.setUpdatedAt(LocalDateTime.now());
//        announcement.setIsFound(data.getIsFound());
//
//
//        // Обновляем фото если есть новое
////        if (photo != null && !photo.isEmpty()) {
////            String photoUrl = "/uploads/" + id + "_" + photo.getOriginalFilename();
////            announcement.setPhotoUrl(photoUrl);
////            System.out.println("🖼️ Фото обновлено: " + photoUrl);
////        }
//
//        System.out.println("✅ Объявление обновлено: " + data.getTitle());
//        return ResponseEntity.ok(announcement);
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<?> getUserAnnouncements(@PathVariable String userId) {
//        System.out.println("📋 Запрос объявлений пользователя: " + userId);
//
//        List<ResponseAnnouncement> userAnnouncements = announcements.values().stream()
//                .filter(a -> a.getUserId().equals(userId))
//                .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
//                .collect(java.util.stream.Collectors.toList());
//
//        System.out.println("✅ Найдено объявлений: " + userAnnouncements.size());
//        return ResponseEntity.ok(userAnnouncements);
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<?> getAllAnnouncements() {
//        System.out.println("📋 Запрос всех объявлений");
//
//        // Возвращаем все объявления из временного хранилища
//        List<ResponseAnnouncement> allAnnouncements = new ArrayList<>(announcements.values());
//
//        // Сортируем по дате создания (новые сначала)
//        allAnnouncements.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
//
//        System.out.println("✅ Отправлено объявлений: " + allAnnouncements.size());
//        return ResponseEntity.ok(allAnnouncements);
//    }
//
//    @PostMapping("/respond")
//    public ResponseEntity<?> respondAnnouncement(@RequestParam("id") String id) {
//        return ResponseEntity.ok(true);
//    }
//
//    @PostMapping("/close")
//    public ResponseEntity<?> closeAnnouncement(@RequestParam("id") String id) {
//        return ResponseEntity.ok(true);
//    }
//}