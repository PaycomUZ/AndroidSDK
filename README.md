# Paycom Android SDK

Paycom Android SDK — это библиотека для интеграции [Paycom](http://paycom.uz/) c вашим мобильным приложением. 

## Подключение библиотеки к мобильному приложению

1. Добавьте в app build.gradle:

```
dependencies {
   compile 'uz.paycom:payment:1.0.4'
}
```

2. Встройте в приложение вызов на оплату:
```
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
```
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

- verify — результат проверки на принадлежность карты. Проверка производится по смс.

[Подробности](http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-plastikovymi-kartami-klientskaya-chast)

## Как выглядит

![Screenshot](docs/img.png?raw=true "Screens")
