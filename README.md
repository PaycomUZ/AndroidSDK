# Paycom Android SDK

Чтобы интегрировать Paycom с мобильным приложением, подключите к мобильному приложению [библиотеку Paycom Android SDK](https://github.com/PaycomUZ/AndroidSDK) и реализуйте методы работы с [пластиковыми картами](http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-plastikovymi-kartami-servernaya-chast) и [чеком](http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-chekom-servernaya-chast) из [Subscribe API](http://paycom.uz/api/#subscribe-api).

В библиотеке Paycom Android SDK — реализован пользовательский интерфейс и все [методы работы с пластиковыми картами для клиентской части](http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-plastikovymi-kartami-klientskaya-chast). 

## Подключение библиотеки

1. Добавьте в app build.gradle:

```java
dependencies {
   compile 'uz.paycom:payment:1.0.5'
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
   PaycomSandBox.setEnabled(true); //true для тестовой площадки, по умолчанию false
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

- number — маскированный номер карты;

- expire — срок действия карты; 

- token — токен для совершения платежа. Токен передаётся в backend мобильного приложения;

- recurrent — флаг, возможность повторного списания;

- verify — верификация карты по смс.

## Режим "песочницы"

* СМС код всегда "666666" в режиме песочницы
* Тестовые карты:
   * 5555 5555 5555 5555      &nbsp; 04/20
   * 4444 4444 4444 4444      &nbsp; 04/20
   * 3333 3333 3333 3333      &nbsp; 04/20 
   * 2222 2222 2222 2222      &nbsp; 04/20 &nbsp; Не подключенно СМС информирование
   * 1111 1111 1111 1111      &nbsp; 04/15 &nbsp; Срок дейтвия истек
   * 8600 0000 0000 0001      &nbsp; 04/20 &nbsp; Карта заблокированнна
   * 8600 0000 0000 0002      &nbsp; 04/20

## Пользовательский интерфейс

![Screenshot](docs/img.png?raw=true "Screens")
