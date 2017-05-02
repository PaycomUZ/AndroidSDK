# Payme Android SDK (https://payme.uz)
===================

Данную библиотеку можно использовать для внедрения оплаты Payme в ваше мобильное приложение.
Для подключения библиотеки проделайте следующие действия:

1. Добавьте в project build.gradle:
...
allprojects {
  repositories {
    jcenter()
    maven {
      url  "http://dl.bintray.com/paycom/general"
    }
  }
}
...

2. Добавте в app build.gradle:
...
dependencies {
   releaseCompile('uz.paycom:payment:1.0:release@aar')
   //Для тестирования интеграции на тестовой площадке
   //debugCompile('uz.paycom:payment:1.0.3:debug@aar') 
}
...

3. Далее, вызов на оплату:
...
@Override public void onClick(View v) {
        Intent intent = new Intent(YourActivity.this, PaymentActivity.class);
        intent.putExtra(EXTRA_ID, xAuth); //ID мерчанта
        final Double sum = Double.valueOf(activityTestSum.getText().toString());
        intent.putExtra(EXTRA_AMOUNT, sum); //Сумма оплаты
        intent.putExtra(EXTRA_SAVE, activityTestMultiple.isChecked()); //Сохранить для многократной оплаты?
        intent.putExtra(EXTRA_LANG, "RU"); //Язык "RU" или "UZ"
        startActivityForResult(intent, 0);
}
...

4. Получаем результат:
...
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
...

где, Result - {number - номер карты маскированный, expire - срок действия, token - токен необходимый для списания оплаты (передается на backend приложения), recurent - возможно ли произвести повторное списание, verify - прошла ли проверка на принадлежность карты владельцу по sms

5. Готово. Побробности: http://paycom.uz/api/#subscribe-api-metody-dlya-raboty-s-plastikovymi-kartami-klientskaya-chast
