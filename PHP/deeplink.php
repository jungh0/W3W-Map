<?php

$db_host="localhost";   
$db_user="thousand419";
$db_passwd=""; 
$db_name="thousand419";

$key = $_REQUEST["key"];


//��ũ �߰� 
// ������ �۵��ϴ°� : echo "<a href='w3w://start'>WAY</a>";
// echo "<a href='w3w://start/$key'>WAY</a>";
//echo "<a href='intent://start/$key/#Intent;scheme=way_w3w;package=com.jungh0.w3w_map;end'>WAY</a>";
//Header ("Location:intent://start/$key/#Intent;scheme=way_w3w;package=com.jungh0.w3w_map;end");
echo("<script>location.replace('intent://start/$key/#Intent;scheme=way_w3w;package=com.jungh0.w3w_map;end');</script>"); 
//echo "<meta http-equiv='refresh' content='0'; url='intent://start/$key/#Intent;scheme=way_w3w;package=com.jungh0.w3w_map;end'>"; 
//echo "<meta http-equiv='refresh' content='0'; url='https://play.google.com/store/apps/details?id=com.jungh0.w3w_map'>"; 


// MySQL - DB ����.

$conn = mysqli_connect($db_host,$db_user,$db_passwd,$db_name);

if (mysqli_connect_errno()){

echo "MySQL ���� ����: " . mysqli_connect_error();

exit;

} else {

}



// ���ڼ� ����, utf8.

mysqli_set_charset($conn,"utf8"); 




mysqli_close($conn);

?>