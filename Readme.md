# Многопользовательский сетевой чат в командной строке (Java).

Данная программа реализует функционал сетевого чата, посредством командной строки.

## Состав

В составе репозитория 2-е программы.
1. Программа для клиента
   1. Main - основной поток предварительного знакомства (имя клиента, синхронизация истории)
   2. Client - класс клиента с инициализацией сокета ... входящего, исходящего потока ... методов класса
   3. InputThread - Runnable класс отвечающий за получение сообщений (запускается после предварительного обмена в классе Main)
2. Программа для сервера
   1. Server - инициализация сервера и создание 2-х потоков (1. ConnectExecutor-предварительное знакомство 2.OutputThread-поток отправки сообщений клиентам). ТАк же хранит список всех активных клиентов и связанный список сообщений.
   2. ConnectExecutor - Runnable класс клиентского соединения с предварительным знакомством 
   3. OutputThread - Поток-слушатель коллекции сообщений и отправитель клиентам
   4. Logger - Singleton класс записи сообщений в файл с историей

## **Схема работы**

1. Запускается сервер (порт указан в коде). Каждое подключение к серверу отбрасывается в отдельный поток, без ограничения их количества.
2. Создается коллекция для получения (отправки) сообщений от клиентов(клиентам).
3. Запускается клиент (ip и порт подключения указан в файле src\main\resources\ClientCfg.ini)
4. Сервер направляет запрос клиенту на указания имени для чата, на который отвечает клиент.
5. Сервер направляет запрос клиенту на создание файла с историей (возможно лишнее). Клиент отвечает о создании или падает(ограничение прав на создание). При наличии файла, клиент следом направляет сквозной номер последнего полученного сообщения, из своего файла с историей (номера сообщений генерируются автоматически на сервере).
6. Сервер читает свой файл с историей и отправляет новые сообщения клиенту для обновления истории у клиента. Обновленная информация записывается в файл с историей и дублируется на экран как пропущенные сообщения.
7. По завершению синхронизации истории, сервер сообщает о регистрации пользователя в чате с полученном ранее именем.
8. Клиент создает отдельный поток-демон(InputThread.class), который обрабатывает все входящие сообщения от сервера.
9. Далее каждое сообщение отправленное клиентом записывается в файл истории сервера и добавляется в потокобезопасную коллекцию.
10. ДАлее поток сервера (OutputThread) слушает связанный список сообщений и отправляет эти соообщения активным клиентам.
11. Клиент получает сообщение, записывает в файл истории и выводит на экран (если это сообщение отправлял не он)

### **Реализованный паттерн**
**Singletone** - объект с файлом историй на сервере и сам сервер(переработано для предполагаемого использования Mockito)
