<?php
// echo '<pre>';

// シェルコマンド "ls" の全ての結果を出力し、出力の最後の
// 行を $last_line に格納します。シェルコマンドの戻り値は
// $retval に格納されます。
// $last_line = system('./hello', $retval);
// system('./hello', $retval);
echo exec('./hello', $retval);

// 追加情報を表示します。
// echo '
// </pre>
// <hr />Last line of the output: ' . $last_line . '
// <hr />Return value: ' . $retval;
?>
