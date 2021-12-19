#**Многопользовательский сетевой чат в командной строке (Java).**
Данная программа реализует функционал сетевого чата, посредством командной строки.

В составе репозитория 2-е программы. 
1. Программа для клиента
2. Программа для сервера

Логика программы по синхронизации данных завязана на синхронизации истории сервера и клиента, который можно разделить на 2 этапа.
Первый на этапе регистрации пользователя, а второй после каждого отправленного сообщения пользователя.
Во втором этапе так же происходит вывод непрочитанных сообщений на экран.

**Схема работы**
1. Запускается сервер (порт указан в коде). Каждое подключение к серверу отбрасывается в отдельный поток, без ограничения их количества.
2. Запускается клиент (ip и порт подключения указан в файле src\main\resources\ClientCfg.ini)
3. Сервер направляет запрос клиенту на указания имени для чата, на который отвечает клиент.
4. Сервер направляет запрос клиенту на создание файла с историей (возможно лишнее). Клиент отвечает о создании или падает(ограничение прав на создание). При наличии файла, клиент следом направляет сквозной номер последнего полученного сообщения, из своего файла с историей (номера сообщений генерируются автоматически на сервере).
5. Сервер читает свой файл с историей и отправляет новые сообщения клиенту для обновления истории у клиента(история не выводится на экран).
6. По завершению синхронизации истории, сервер сообщает о регистрации пользователя в чате с полученном ранее именем.
7. Клиент получает сообщение и может отправлять сообщения в общий чат. Каждое сообщение и последний номер из истории отправляется на сервер.
8. Сервер получает сообщение, записывает в историю, и отправляет пользователю новые сообщения из своего файла ... те которые пользователь еще не получал(сообщения других пользователей)
9. Клиент получает новые сообщения, в том числе и направленное на этапе 7, и записывает их в свою историю(файл).
10. п.7-9 в цикле до завершения сеанса с одной из сторон или отправки клиентом "/exit"

**Реализованный паттерн**
Singletone на создание объекта с файлом историй на сервере

**Возможные варианты улучшения**
1. Перевод на хранения истории сервера в потокобезопасной коллекции.
2. Использование по 2 потока. 

Сервер: 1-ый постоянного слушает и записывает сообщения от клиента. 2-й постоянно отслеживает обновления в истории и отправляет всем клиентам. 

Клиент: 1-ый отправляет сообщения. 2-ой постоянно слушает 2-ой поток сервера и записывает историю.

Позволит ли командная строка сделать такую гибкую программу, что бы новые сообщение добавлялись до положения каретки? Как вставлять новые сообщения от других пользователей смещая каретку текущего пользователя вниз?... а если он уже начал писать сообщение?  