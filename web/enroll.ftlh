[#ftl output_format="HTML"]
<!DOCTYPE html>
<html lang="en">
<head><meta charset="UTF-8"><meta http-equiv="X-UA-Compatible" content="IE=edge"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>2FA
	</title><link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2/dist/tailwind.min.css" type="text/css"></head>
<body>

  
	
	
	
	
	
  
  
	<div class="md:h-screen bg-white relative flex flex-col justify-center items-center">
	  <div class="md:border md:border-gray-300 bg-white md:shadow-lg shadow-none rounded p-10">
		<div class="flex flex-col items-center space-y-3">
		  <div class="flex">
			<img src="gluulogo.png">
		  </div>
		  <p class="leading-normal">1. Scan code
		  </p>
		  <div id="qrcode">
		  </div>
		</div>
		<form method="post" class="my-8">
		  <p class="leading-normal">2. Enter code
		  </p>
		  <div class="relative mb-2">
			<input type="text" id="code" name="code" autofocus="" placeholder="******" class="w-full rounded px-3 border border-gray-300 pt-2 pb-2">
		  </div>
      <p class="leading-normal">3. Nickname (optional)
		  </p>
		  <div class="relative mb-2">
			<input type="text" id="nickName" name="nickName" autofocus="" placeholder="" class="w-full rounded px-3 border border-gray-300 pt-2 pb-2">
		  </div>
		  <div class="space-y-9">
			<div class="text-sm flex justify-between items-center">
			  <button type="submit" class="py-2 px-6 rounded text-white btn bg-green-500 hover:bg-green-600">
				Submit
			  </button>
			</div>
		  </div>
		</form>
	  </div>
	</div>
  
  
  
</body>

<script type="text/javascript" src="jquery.min.js"></script>
<script type="text/javascript" src="jquery-qrcode.min.js"></script>

<script>
      var QR_CODE_DEFAULT_OPTIONS = {
        // render method: 'canvas', 'image' or 'div'
        render: "canvas",

        // version range somewhere in 1 .. 40
        minVersion: 1,
        maxVersion: 40,

        // error correction level: 'L', 'M', 'Q' or 'H'
        ecLevel: "M",

        // offset in pixel if drawn onto existing canvas
        left: 0,
        top: 0,

        // size in pixel
        size: 300,

        // code color or image element
        fill: "#000",

        // background color or image element, null for transparent background
        background: null,

        // content
        text: "${secretKeyUri!}",

        // corner radius relative to module width: 0.0 .. 0.5
        radius: 0,

        // quiet zone in modules
        quiet: 0,

        // modes
        // 0: normal
        // 1: label strip
        // 2: label box
        // 3: image strip
        // 4: image box
        mode: 2,

        mSize: 0.1,
        mPosX: 0.5,
        mPosY: 0.5,

        label: "${qrCodeLabel!}",
        fontname: "sans",
        fontcolor: "#000",
        image: null,
      };

	window.onload = function () {
	  var form = document.querySelector("form");
	  form.onsubmit = handleFormValidation;
    $("#qrcode").qrcode(QR_CODE_DEFAULT_OPTIONS);
	};
	function handleFormValidation() {
	  var code = document.getElementById('code').value
	  if (code === '') {
		alert('Code is required')
		return false
	  }
	  return true
	}
  </script>
</html>