<!DOCTYPE html>
<html >
  <head>
    <meta charset="UTF-8">
    <title>Učitaj raspored</title>
    
    
    
    
        <link rel="stylesheet" href="../css/style.css">

    
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    
  </head>

  
  <body>
	
	<?php
		session_start();
		if(empty($_SESSION['guid'])) {
			header('Location: ../index.php');
			exit();
		}
	?>
	
    <div class="login-page">
  <div class="form">
    <form class="login-form" action="/php/uploadfile.php" method="post" enctype="multipart/form-data">
		<?php
		if ($_SESSION["error"] != "") {
				echo "<p class=\"errormsg\">" . $_SESSION["error"] . "</p>";
				$_SESSION["error"] = "";
			}
		?>
		
		<label class="control-label">Odaberite datoteku</label>
		<input id="input-1a" type="file" class="file" name="fileUpload" data-show-preview="false">
		
		<button type="submit">učitaj</button>
		<p>Prijavljeni ste kao: <strong><?php echo $_SESSION['username'];?></strong></p>
    </form>
  </div>
</div>
    <script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>

        <script src="js/index.js"></script>

    
    
    
  </body>
</html>
