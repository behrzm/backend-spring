/**
 * ИНТЕГРАЦИЯ ANDROID КЛИЕНТА С BACKEND СЕРВЕРОМ
 * 
 * Все изменения, которые были сделаны для корректной работы с реальными данными:
 */

// ============================================================================
// ✅ ЧТО БЫЛО ИСПРАВЛЕНО:
// ============================================================================

1. API ENDPOINTS (CodeQuestApi.kt)
   ❌ Старое: GET "api/profile/{userId}" 
   ✅ Новое: GET "api/v1/profiles/me" (используется текущий пользователь из JWT)
   
   ❌ Старое: POST "api/profile"
   ✅ Новое: PUT "api/v1/profiles/me"
   
   ✅ Добавлены новые endpoints:
      - POST /api/v1/profiles/me/wins
      - GET + PUT /api/v1/levels/progress
      - GET + PUT /api/v1/levels/learning-context
      - GET /api/v1/xp/history

2. FIREBASE АУТЕНТИФИКАЦИЯ
   ✅ Создан FirebaseAuthInterceptor.kt
      - Автоматически добавляет Firebase ID Token в заголовок Authorization
      - Для каждого запроса извлекает текущий токен из FirebaseAuth
      - Backend верифицирует токен и узнает пользователя

3. API МОДЕЛИ (ApiModels.kt)
   ✅ Обновлены все модели данных:
      - ProfileResponse: добавлены email, avatar_url
      - LeaderboardResponse: использует "id" вместо "user_id"
      - Новые классы для level_progress, learning_context, xp_history

4. RETROFIT КОНФИГУРАЦИЯ (ApiLeaderboardRepository.kt)
   ✅ Добавлен OkHttpClient с FirebaseAuthInterceptor
   ✅ Все запросы теперь автоматически получают Authorization заголовок

5. BACKEND URL (ProfileViewModel.kt)
   ⚠️  ВАЖНО: По умолчанию стоит http://10.0.2.2:8080/
   Это работает для Android эмулятора на localhost

// ============================================================================
// ⚙️ КОНФИГУРАЦИЯ ПЕРЕД ЗАПУСКОМ:
// ============================================================================

STEP 1: Выберите правильный URL бэкенда
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Файл: app/src/main/java/.../ui/profile/ProfileViewModel.kt
Сроки 26:

   // ВАРИАНТ 1: Android эмулятор + локальный бэкенд
   private val repository: LeaderboardRepository = 
       ApiLeaderboardRepository("http://10.0.2.2:8080/")

   // ВАРИАНТ 2: Физический телефон + локальный бэкенд
   private val repository: LeaderboardRepository = 
       ApiLeaderboardRepository("http://192.168.1.XXX:8080/")
       // Замените XXX на IP адрес вашего компьютера в сети

   // ВАРИАНТ 3: Production сервер
   private val repository: LeaderboardRepository = 
       ApiLeaderboardRepository("https://api.codequest.example.com/")

STEP 2: Убедитесь что Firebase конфигурирован
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

✓ Файл google-services.json должен быть в app/ папке
✓ Firebase проект должен быть создан в Firebase Console
✓ Email/Password аутентификация включена в Firebase

STEP 3: Запустите бэкенд
━━━━━━━━━━━━━━━━━━━━━━

Откройте backend-spring проект и запустите:
   ./gradlew bootRun
   
Или в IntelliJ IDEA:
   Run → Run 'CodeQuestApplication'

Убедитесь что API работает:
   curl http://localhost:8080/api/v1/health

// ============================================================================
// 🔍 КАК ЭТО РАБОТАЕТ:
// ============================================================================

FLOW аутентификации и получения данных:
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. Пользователь логинится через Android UI
   └─→ FirebaseAuth.signInWithEmailAndPassword()

2. При запросе профиля (ProfileViewModel.refresh()):
   └─→ ApiLeaderboardRepository.fetchCurrentUser(uid)
       └─→ CodeQuestApi.getProfile() (GET /api/v1/profiles/me)
           └─→ FirebaseAuthInterceptor добавляет Authorization заголовок
               └─→ Backend получает запрос с Bearer токеном
                   └─→ Security фильтр верифицирует токен
                       └─→ Если токен валиден → вернуть данные текущего пользователя
                       └─→ Если токен невалиден → вернуть 401 Unauthorized

3. Backend использует uid из токена для идентификации
   └─→ Если профиля нет в БД → создаёт автоматически со значениями по умолчанию
   └─→ Если профиль есть → возвращает его данные из Supabase

// ============================================================================
// ⚠️ ВОЗМОЖНЫЕ ПРОБЛЕМЫ И РЕШЕНИЯ:
// ============================================================================

ПРОБЛЕМА 1: "Unable to resolve dependency for Retrofit"
РЕШЕНИЕ: Синхронизируйте Gradle
   → File → Sync Now
   → Или запустите: ./gradlew dependencies

ПРОБЛЕМА 2: "Authorization: Bearer null" в логах бэкенда
РЕШЕНИЕ: Пользователь не авторизован в Firebase
   → Убедитесь что пользователь вошёл (FirebaseAuth.currentUser != null)
   → Проверьте google-services.json
   → Попробуйте пересоздать пользователя

ПРОБЛЕМА 3: "Connection refused: 10.0.2.2:8080"
РЕШЕНИЕ: Бэкенд не запущен или порт неправильный
   → Запустите бэкенд: ./gradlew bootRun
   → Проверьте порт в application.properties (по умолчанию 8080)
   → Убедитесь что firewall не блокирует порт

ПРОБЛЕМА 4: "CORS error" в браузере консоли эмулятора
РЕШЕНИЕ: Это обычно, CORS не используется для native приложений
   → Проверьте детали ошибки, может быть что-то другое

ПРОБЛЕМА 5: "401 Unauthorized" от бэкенда
РЕШЕНИЕ: Проблема с Firebase tokenом
   → Проверьте что google-services.json правильный
   → Убедитесь что пользователь аутентифицирован
   → Посмотрите логи бэкенда для деталей

// ============================================================================
// 🧪 ТЕСТИРОВАНИЕ ИНТЕГРАЦИИ:
// ============================================================================

СПОСОБ 1: Через Postman (для бэкенда)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. Откройте Postman коллекцию (находится в backend-spring/docs)
2. Получите Firebase ID token:
   curl -X POST https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=YOUR_API_KEY \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"password","returnSecureToken":true}'
3. Скопируйте идентификационный токен (idToken)
4. В Postman добавьте заголовок:
   Authorization: Bearer <YOUR_ID_TOKEN>
5. Тестируйте endpoints через GUI

СПОСОБ 2: Через Android Debug Bridge (для клиента)
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. Запустите Android приложение на эмуляторе
2. Откройте Android Studio Logcat
3. Фильтруйте логи: package:mine
4. Смотрите логи Firebase и Retrofit:
   D/Retrofit: --> GET /api/v1/profiles/me
   D/Retrofit: Authorization: Bearer eyJhbGci...
5. Нажимайте на UI элементы и смотрите сетевые запросы

// ============================================================================
// 📚 ДОПОЛНИТЕЛЬНЫЕ РЕСУРСЫ:
// ============================================================================

Backend:
   - Backend README: ../backend-spring/README.md
   - API документация: http://localhost:8080/api/v1/swagger-ui.html
   - Исходный код: ../backend-spring/src/main/java

Android:
   - Firebase Auth: https://firebase.google.com/docs/auth/android/start
   - Retrofit: https://square.github.io/retrofit/
   - OkHttp: https://square.github.io/okhttp/

// ============================================================================
// ✨ ВСЁ ГОТОВО!
// ============================================================================

Теперь приложение:
✅ Получает реальные данные пользователя из Supabase
✅ Отправляет Firebase ID token для аутентификации
✅ Автоматически создаёт профили новых пользователей
✅ Обновляет XP, уровни, прогресс уровней в реальной БД
✅ Полностью защищено (только аутентифицированные пользователи)

Если у вас остались вопросы - смотрите логи и Backend API документацию!

