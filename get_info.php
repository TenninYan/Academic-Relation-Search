<?php
$temp_search_word = $_POST["search_word"];

$search_word = urlencode($temp_search_word);

$url = "http://localhost:8983/solr/mit_course5/select?q=$search_word&wt=csv&indent=true";

$contents=file_get_contents($url);
?>

<html>
<head>
    <meta charset="utf-8">
    <title>Academic Relation Search</title>
    <link rel="stylesheet" href="main.css">
    <link rel="shortcut icon" href="favicon.ico" type="image/vnd.microsoft.icon" >
    <script src="js/tabs.js"></script>
    
</head>

<body>
<div id="header">
    <h1>Academic Relation Search<br></h1>
</div>
<div id="search_main">
    <form action="get_info.php" method="post">
    <input type="text" name="search_word" value="" class="text_sub">
    <input type="submit" value="Search" class="btn_sub">
    </form>

<!-- <div id="mid"> -->
    <?php
    if (!(empty($temp_search_word))){
    print "<h4>";

    print "Searched: ";
    print ($temp_search_word);
    print "</h4>";
    // print "</div>";
    // echo "<hr>";
    
    $csv = str_getcsv($contents);
    $solr_number = " ";
    $i = 0;
    
    foreach ($csv as $row){
        if($i > 1){
            $csv_array = preg_split('/[\s]+/',$row, -1, PREG_SPLIT_NO_EMPTY);
            // print_r ($csv_array[0]);
            $solr_number = $solr_number." "."$csv_array[0]";
            // echo "<hr>";
        }
        $i++;
    }
    
    // print ($solr_number);
    // echo "<hr>";
    
    system ('java Main'.$solr_number,$retval);
    if ($retval !== 0){
        print ("Search Failed");
    }
    
    $file_name = "output.csv";
    $output_file = fopen($file_name,"r");
    
    while (($data = fgetcsv($output_file, 0, ",")) !== FALSE) {
        $csv[] = $data;
    }
    fclose($output_file);
    

    ?>
</div>


<div id="mid">
<div id="sidebar">
<div float="left">
<ul id="tabs">
<?php
$department = "";
$department_number=1;
$department_list = array("none");
if (!(empty($temp_search_word))){
foreach ($csv as $row){
    if (gettype($row)=="array"){
        if ($department !== $row[3]){
            print "<li><a href='#box$department_number'>$row[3]</a></li>";
            $department_number++;
            $department=$row[3];
            array_push($department_list,$row[3]);
        }
    }
}}
    }
?>

</ul>
</div>
</div>

<div id="mainpart">
<ul float = "left">
<?php
if (!(empty($temp_search_word))){
// print"<div id='box1'>";
print"<div>";
$department_number2=1;
foreach ($csv as $row){
    if (gettype($row)=="array"){
        if ($department !== $row[3]){
            print"</div>";
            print "<div id='box$department_number2'>";
            print "<h4>";
            print "Department: $row[3] <br>";
            print "</h4>";
            print "<hr>";
            $department_number2++;
            $department=$row[3];
            array_push($department_list,$row[3]);

        }
        print"<div class='mit_data'>";
        print"<span class='photo-right'><img src='$row[5]' height='200px'></span>";
        // print "Department: $row[3] <br>";
        print "<h3><b>";
        print "$row[0] <br>";
        print "</b></h3>";
        print "Class name: ";
        print "<a href='$row[4]'>";
        print "$row[1]<br>";
        print "</a>";
        print "Level: ";
        print "$row[6]<br>";
        // print "$row[1]";
        // var_dump($row);
        $related_number=7;
        // print Gettype($related_number+1);
        // print ($related_number+1);
        $related_array =array_slice($row,$related_number,count($row)-7);
        // var_dump($related_array);
        // echo 'hello!',$related_array[0];
        print "related class:<br>";
        for($i=0;$i<(((count($row))-7)/2);$i++){
            echo '<a href="',$related_array[$i*2],'">';
            echo $related_array[$i*2+1];
            print "</a><br>";
        }
        print"<div class='mit_data'></div>";
        print"</div>";
        print "<hr>";
    }
}
print "</div>";
print"</ul>";
print'</div>';

// var_dump($department_list);
// print'<div id="sidebar">';
// print'<div float="left">';
// print'<ul id="tabs">';
// for ($i=1;$i<=$department_number;$i++){
//     print"<li><a href='#box$i'>$department_list($i)</a></li>";
// }
// print'</ul>';
// print'</div>';
// print'</div>';
}
?>

<br>
<br>
<br>


</div>
<!-- <div class="box"> -->
<!-- <div id="box1"> -->
<!--     box1 -->
<!-- </div> -->

<!-- <div id="box2"> -->
<!--     box2 -->
<!-- </div> -->
<!--  -->
<!-- <div id="box3"> -->
<!--     box3 -->
<!-- </div> -->
<!--  -->
<!-- </div> -->

<script type="text/javascript">
    window.onload = function() {
    var tabNavigationObj = new tabNavigation({
        boxElementIdName : 'tabs'
    });
};
</script>
<br>
<br>
<br>
<br>


</body>
</html>
