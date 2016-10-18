<!DOCTYPE html>
<html >

	
  <head>
    <meta charset="UTF-8">
    <title>Prijava EFZG Raspored</title>
    
    
    
    
        <link rel="stylesheet" href="css/style.css">

    
    
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
  </head>

  <body>
  
  <?php
		session_start();
	?>

    <div class="login-page">
  <div class="form">
    <form class="login-form" action="/php/login.php" method="post">
		
		<?php
			if ($_SESSION["error"] != "") {
				echo "<p class=\"errormsg\">" . $_SESSION["error"] . "</p>";
				$_SESSION["error"] = "";
			}
		?>
		
		<input type="text" placeholder="korisničko ime" name="user" required="true"/>
		<input type="password" placeholder="lozinka" name="pass" required="true"/>
		<button type="submit">prijava</button>
		
    </form>
  </div>
</div>
    <script src='http://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>

        <script src="js/index.js"></script>

    
    
    
  </body>
</html>
