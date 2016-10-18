<?php
	function loadGroups($xml, $connection)
	{
		echo "\n\r" . 'Loading Groups';
		$connection->query("TRUNCATE TABLE groups");
		foreach($xml->groups->group as $item)
		{
			$id = $connection->real_escape_string($item->attributes()->id);
			$name = $connection->real_escape_string($item->attributes()->name);
			$branch_id = $connection->real_escape_string($item->attributes()->branch_id);
			
			if(isset($item->attributes()->parent_id))
			{
				$parent_id = $connection->real_escape_string($item->attributes()->parent_id);
			}
			else
			{
				$parent_id = $id;
			}
			
			if(!mysqli_query($connection, "INSERT INTO `groups` (id, `name`, branch_id, parent_id) VALUES ($id, '$name', $branch_id, $parent_id);"))
			{
				echo mysqli_error($connection);
			}
		}
	}
?>