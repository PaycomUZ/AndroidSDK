# Paycom Android SDK [![Download](https://api.bintray.com/packages/paycom/general/android-sdk/images/download.svg)](https://bintray.com/paycom/general/android-sdk/_latestVersion)

Чтобы интегрировать Paycom с мобильным приложением, подключите к мобильному приложению [библиотеку Paycom Android SDK](https://github.com/PaycomUZ/AndroidSDK) и реализуйте [методы работы с пластиковыми картами](http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-plastikovymi-kartami-servernaya-chast) и [чеком](http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-chekom-servernaya-chast) из [Subscribe API](http://paycom.uz/api/#subscribe-api).

В библиотеке Paycom Android SDK — реализован пользовательский интерфейс и все [методы работы с пластиковыми картами для клиентской части](http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-plastikovymi-kartami-klientskaya-chast).

[Последняя версия библиотеки Paycom Android SDK на bintray](https://bintray.com/paycom/general/android-sdk)

## Подключение библиотеки

1. Добавьте в app build.gradle:

```java
dependencies {
   compile 'uz.paycom:payment:$last version' 
}
```

2. Встройте в приложение вызов на оплату:
```java
@Override public void onClick(View v) {
   Intent intent = new Intent(YourActivity.this, PaymentActivity.class);
   intent.putExtra(EXTRA_ID, xAuth); //Ваш ID мерчанта
   final Double sum = Double.valueOf(activityTestSum.getText().toString());
   intent.putExtra(EXTRA_AMOUNT, sum); //Сумма оплаты
   intent.putExtra(EXTRA_SAVE, activityTestMultiple.isChecked()); //Сохранить для многократной оплаты?
   intent.putExtra(EXTRA_LANG, "RU"); //Язык "RU" или "UZ"
   PaycomSandBox.setEnabled(false); //true для тестовой площадки
   startActivityForResult(intent, 0);
}
```

## Обработка результата

После вызова оплаты: покупатель вводит данные платежа, Paycom SDK — возвращает токен для совершения платежа. Токен передаётся в backend мобильного приложения. 

**Пример**
```java
@Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      Result result = data.getParcelableExtra(EXTRA_RESULT);
      Log.d(TAG, result.toString());
    } else if (resultCode == RESULT_CANCELED) {
      Log.d(TAG, "Payment canceled"); //Произошла отмена оплаты
    }
  }
```

**Result** содержит поля:

- number: string // Маскированный номер карты;

- expire: string // Срок действия карты; 

- token: string // Токен для совершения платежа. Токен передаётся в backend мобильного приложения и используется для [оплаты чека](http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-chekom-servernaya-chast-oplata-cheka). 

- recurrent: boolean // Возможность проведения повторных платежей. Если false — возможна только одна транзакция с обязательным указанием точно такой же суммы.

- verify: boolean // Была ли пройдена идентификация владельца карты по смс.

## Тестирование в песочнице

СМС-код безопасности для всех тестовых карт всегда: 666666

**Тестовые карты**

| Номер               | Срок действия карты (Expired) | Комментарий                       |
| ------------------- | ----------------------------- | --------------------------------- |
| 8600 0691 9540 6311 | 03/20                         |                                   |
| 8600 4954 7331 6478 | 03/20                         |                                   |
| 8600 0609 2109 0842 | 03/20                         | Не подключенно СМС информирование |
| 3333 3364 1580 4657 | 03/15                         | Срок дейтвия истек                |
| 4444 4459 8745 9073 | 03/20                         | Карта заблокированнна             |

## Пользовательский интерфейс

![Screenshot](docs/img.png?raw=true "Screens")
