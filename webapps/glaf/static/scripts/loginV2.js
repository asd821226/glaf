$(document)
		.ready(
				function() {
					var $x = $("#x").focus();
					function doAxy() {
						var x = $x.val();
						x = encodeURIComponent(x);
						var y = $("#yy").val();
						if (x == "") {
							alert("用户名不能为空。");
							document.getElementById("x").focus();
							return;
						} else if (y == "") {
							alert("密码不能为空。");
							document.getElementById("yy").focus();
							return;
						} else {
							//document.getElementById("loginBt").disabled = true;
							if (map.get("login_status") != null) {
								alert("正在登录,请稍等......");
								return;
							}
							map.put("login_status", "login");
							//$("#loginBt:disabled");
							//delay();
							var userIdx = document.getElementById("x").value;
							jQuery
									.ajax({
										type : "POST",
										url : contextPath
												+ "/login/getToken?userId="+userIdx,
										dataType : 'json',
										error : function(data) {
											// alert('服务器处理错误！');
											//document.getElementById("loginBt").disabled = false;
											map.remove("login_status");
										},
										success : function(dataxy) {
											if (dataxy != null
													&& dataxy.x_y != null
													&& dataxy.x_z != null
												    && dataxy.public_key != null) {
												var px = dataxy.x_y + y
														+ dataxy.x_z;
												var crypt2 = new JSEncrypt();
												//alert(dataxy.public_key);
												//alert(px);
		                                        crypt2.setKey(dataxy.public_key);
		                                        var str = crypt2.encrypt(px);
												//var str = encryptedString(dataxy.public_key, str, 'NoPadding', 'UTF-8');
												//alert(str);
												document.getElementById("y").value = str;
												document.getElementById("yy").value = str;

												var link = contextPath
														+ "/login/doLogin?x="
														+ x +"&token="+dataxy.token
														+ "&responseDataType=json";
												var params = jQuery("#iForm")
														.formSerialize();
												//alert(params);
												jQuery
														.ajax({
															type : "POST",
															url : link,
															dataType : 'json',
															data : params,
															error : function(
																	data) {
																// alert('服务器处理错误！');
																map.remove("login_status");
																$("#loginBt:enabled");
															},
															success : function(
																	data) {
																if (data != null) {
																	if (data.statusCode == 200) {// 登录成功
																		map
																				.remove("login_status");
																		//alert("登录成功,准备跳转...");
																		window.location = contextPath
																				+ "/my/home";
																	} else {
																		if (data.message != null) {
																			map.remove("login_status");
																			alert(data.message);
																			document.getElementById("y").value = "";
																			document.getElementById("yy").value = "";
																			document.getElementById("x_y").value = "";
																			document.getElementById("x_z").value = "";
																			$("#loginBt:enabled");
																		}
																	}
																} else {
																	// alert('服务器处理错误！');
																	map.remove("login_status");
																	$("#loginBt:enabled");
																}
															}
														});
											}
										}
									});
						}
					}

					$("#loginBt").click(function() {
						doAxy();
					});
					 
					document.onkeydown = function(event) {
						var e = event || window.event
								|| arguments.callee.caller.arguments[0];
						if (e && e.keyCode == 13) {
							doAxy();
						}
					};
	});
 