import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    final String TOKEN = "1674151513:AAE9AnAEV_XzUkQbfwySo_FN1_ONLSN9AOc";
    HashMap<Long, User> userList = new HashMap<>();

    public String getBotUsername() {
        return "ROSN_OD_bot";
    }

    public String getBotToken() {
        return TOKEN;
    }

    public void onUpdateReceived(Update update) {

        if (!userList.containsKey(update.getMessage().getChatId())) {
            User user = new User(update.getMessage().getChatId());
            user.setState(User.States.NEW);
            user.setUserName(update.getMessage().getChat().getFirstName());
            user.setUserSurname(update.getMessage().getChat().getLastName());
            userList.put(update.getMessage().getChatId(), user);
        }

        User user = userList.get(update.getMessage().getChatId());
        User.States state = userList.get(update.getMessage().getChatId()).getState();
        String messageText = update.getMessage().getText().toLowerCase();
        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());

        switch (messageText) {
            case ("/start"):
                messageStateNew(message);
                userList.get(update.getMessage().getChatId()).setState(User.States.NEW);
                break;
            case ("help"):
                messageHelp(message);
                break;
            case ("start"):
            case ("старт"):
                messageStateStart(message);
                user.setState(User.States.AIR_PRESSURE_MIN);
                break;
            case ("userlist"):
                message.setText(userList.toString());
                System.out.println(userList.toString());
                break;
            default:
                if (!isNaN(messageText)) {
                    int numeric = Integer.parseInt(messageText);
                    if (state == User.States.AIR_PRESSURE_MIN) {
                        if (numeric > 100 && numeric < 320) {
                            messagePressureMin(update, message);
                            user.setState(User.States.AIR_PRESSURE_PATHWAY);
                            break;
                        } else {
                            message.setText("Неверное значение! Введите значене минимального давления в звене в пределах 100 - 320 бар.");
                            break;
                        }
                    }
                    if (state == User.States.AIR_PRESSURE_PATHWAY) {
                        if (numeric >= 0 && numeric < 21) {
                            messagePressurePathway(update, message);
                            break;
                        } else {
                            message.setText("Неверное значение! Введите значене времени в пути в пределах 0 - 20 мин. " +
                                    "Если звено затратило на дорогу к очагу больше 20 мин. и оно укомплектовано " +
                                    "однобаллонными АСВ объемом 6.8 л., ему незамедлительно нужно начать выход на свежий воздух!");
                            break;
                        }
                    }
                }
                message.setText("Вас не понял. Повторите.");
        }


//        System.out.println(update.getMessage().getChatId());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // Notification to myself
        try {
            execute(notification(update));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isNaN(String text) {
        try {
            int pressure = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }

    public SendMessage notification(Update update) {
        SendMessage message = new SendMessage();
        message.setChatId("1171284117");
        message.setText(
                update.getMessage().getChatId().toString() +
                        "\nName: " + update.getMessage().getChat().getFirstName() +
                        "\nSurname: " + update.getMessage().getChat().getLastName() +
                        "\nLocation: " + update.getMessage().getChat().getLocation() +
                        "\nMessage: " + update.getMessage().getText()
//                .concat(update.getMessage().getContact().getPhoneNumber())
//                update.getMessage().toString().substring(0)
        );
        return message;
    }

    private void messageHelp(SendMessage message) {
        message.setText("Приказ МЧС №139 Приложение 15 \n5.\tКаждый газодымозащитник перед входом в непригодную для дыхания среду обязан сдать жетон постовому на посту безопасности, а при возвращении получить его.\n" +
                "6.\tПостовой на посту безопасности, собрав жетоны, сверяет записи с докладами газодымозащитников, проводит расчет ожидаемого времени выхода (Твых) звена ГДЗС и переносит записи в журнал учета работающих звеньев ГДЗС. \n" +
                "7.\tЖетоны закрепляются на рабочей доске постового на посту безопасности, верхним закрепляется жетон АСВ с наименьшим защитным временем.\n\n\nПриказ МЧС №139 п.31.\nПостовым на посту безопасности назначается газодымозащитник, допущенный к выполнению своих обязанностей приказом начальника ОПЧС. Он обязан: \n" +
                "- перед входом звена в непригодную для дыхания среду проверить наличие датчиков неподвижного состояния, принять личные жетоны от газодымозащитников, внести показания манометров в журнал поста безопасности ГДЗС;\n" +
                "- контролировать время работы звена ГДЗС, давление, при котором звену необходимо начать движение на выход из непригодной для дыхания среды, проводить расчеты согласно приложению 8;\n" +
                "- осуществлять контроль за количеством газодымозащитников, ушедших в непригодную для дыхания среду и возвратившихся из нее;\n" +
                "- при работе через каждые 10 минут, а при необходимости чаще, информировать командира звена о давлении, при котором звену необходимо начать движение на выход из непригодной для дыхания среды, времени, прошедшем с момента включения в АСВ, времени работы у места ликвидации ЧС, по истечении которого звену необходимо возвращаться на чистый воздух; \n" +
                "- поддерживать связь со звеном, работающим в непригодной для дыхания среде, с помощью средств связи, выполнять указания командира звена;\n" +
                "- при нарушении связи со звеном, поступлении сообщения о несчастном случае или задержке его возвращения немедленно докладывать об этом РТП, НБУ или начальнику контрольно-пропускного пункта (далее – НКПП) и действовать в соответствии с их указаниями;\n" +
                "- поддерживать связь с НБУ или НКПП (в отдельных случаях посту безопасности может быть придан связной);\n" +
                "- передавать сведения, поступившие от звена ГДЗС НКПП, НШ или РТП;\n" +
                "- не допускать скопления людей у входа в задымленное помещение;\n" +
                "- не допускать в непригодную для дыхания среду работников (людей) без АСВ, а также имеющих АСВ, но не входящих в состав звена или имеющих АСВ разного типа;\n" +
                "- вести наблюдение за обстановкой по внешним признакам, состоянием строительных конструкций в месте размещения поста безопасности, обо всех изменениях немедленно докладывать НБУ и командиру звена, находящемуся в непригодной для дыхания среде. Если звену грозит опасность, немедленно вызвать его на свежий воздух, доложить об этом НБУ или РТП.\n");
    }

    private void messageStateNew(SendMessage message) {
        message.setText("Тестовый режим!\n" +
                "help - обязанности постового на ПБ\n" +
                "start - расчеты при работе в АСВ");

        List<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("help"));
        keyboardRows.add(keyboardRow);

        keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("start"));
        keyboardRows.add(keyboardRow);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(keyboardRows);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboard);
    }

    private void messageStateStart(SendMessage message) {
        message.setText("Выбери или введи минимальное давление воздуха в АСВ в звене:");

        List<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardRow = new KeyboardRow();

        for (int i = 240, j = 1; i < 301; i+= 5, j++) {
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(Integer.toString(i));
            keyboardRow.add(keyboardButton);
            if (j % 3 == 0) {
                keyboardRows.add(keyboardRow);
                keyboardRow = new KeyboardRow();
            }
        }

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(keyboardRows);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboard);
    }

    private void messagePressureMin(Update update, SendMessage message) {
        User user = userList.get(update.getMessage().getChatId());
        user.setAirPressureMin(Integer.parseInt(update.getMessage().getText()));
        user.setExitTime((int) Math.floor((double) (user.getAirPressureMin() - user.getAIR_PRESSURE_RESERVE()) / 40 * 6.8));
        message.setText("Минимальное давление в звене: " + user.getAirPressureMin() + " (" + user.getExitTime() + "мин до свистка" + ")" + "\n");
        message.setText(message.getText().concat("Засеки и введи время в минутах, затраченное звеном на дорогу к месту выполнения работ"));

        List<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardRow = new KeyboardRow();

        for (int i = 1, j = 1; i < 17; i++, j++) {
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(Integer.toString(i));
            keyboardRow.add(keyboardButton);
            if (j % 4 == 0) {
                keyboardRows.add(keyboardRow);
                keyboardRow = new KeyboardRow();
            }
        }

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(keyboardRows);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboard);
    }

    private void messagePressurePathway(Update update, SendMessage message) {
        User user = userList.get(update.getMessage().getChatId());
        user.setPathwayTime(Integer.parseInt(update.getMessage().getText()));
        user.setAirPressurePathway(40 / 6.8 * user.getPathwayTime());
        user.setAirPressureToExit(user.getAirPressurePathway() * 1.5 + user.getAIR_PRESSURE_RESERVE());
        user.setWorkingTime((user.getAirPressureMin() - user.getAirPressurePathway() - user.getAirPressureToExit()) / 40 * 6.8);
        message.setText("Минимальное давление в звене: ".concat(String.valueOf(user.getAirPressureMin())));
        message.setText(message.getText().concat("\n"));
        message.setText(message.getText().concat("Для АСВ с 1 баллоном 6.8 л.: "));
        message.setText(message.getText().concat("\n"));
        message.setText(message.getText().concat("Давление входа: ".concat(String.format("%.1f", user.getAirPressurePathway()))));
        message.setText(message.getText().concat("\n"));
        message.setText(message.getText().concat("Время работы \"у очага\": ".concat(String.format("%.1f", user.getWorkingTime()))));
        message.setText(message.getText().concat("\n"));
        message.setText(message.getText().concat("Давление выхода: ".concat(String.format("%.1f", user.getAirPressureToExit()))));
        message.setText(message.getText().concat(" (при этом давлении в АСВ звено должно незамедлительно начать выход из НДС!)"));

        List<KeyboardRow> keyboardRows = new ArrayList<KeyboardRow>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("help"));
        keyboardRows.add(keyboardRow);

        keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("start"));
        keyboardRows.add(keyboardRow);

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setKeyboard(keyboardRows);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        message.setReplyMarkup(keyboard);
    }

    }
