package org.kevin.k8s_api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonSyntaxException;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1beta2Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1DeleteOptions;
import io.kubernetes.client.models.V1Namespace;
import io.kubernetes.client.models.V1PersistentVolume;
import io.kubernetes.client.models.V1PersistentVolumeClaim;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.models.V1Status;
import io.kubernetes.client.models.V1beta2Deployment;

@RestController
public class Controller {
	
	private V1DeleteOptions options;
	private String pretty = "false";
	
	@Bean
	public V1DeleteOptions initDeleteOption() {
		options = new V1DeleteOptions();
		options.setPropagationPolicy("Foreground");
		return options;
	}
//	options.setPropagationPolicy("Foreground");
	
	@RequestMapping(method = RequestMethod.GET, path = "list_pod_for_all_namespace")
	@ResponseBody
	public RespObject listPod() {
		CoreV1Api api = new CoreV1Api();
		V1PodList list;
		try {
			list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
			List<String> names = new ArrayList<>();
			for (V1Pod item : list.getItems()) {
				names.add(item.getMetadata().getName());
			}
			return new RespObject(names);
		} catch (ApiException e) {
			e.printStackTrace();
			return new RespObject(e.getMessage(), 1);
		}
	}

	/**
	 * 部署
	 * 
	{
		"kind": "Deployment",
		"metadata": {
			"namespace": "default",
			"name":"test-dep",
			"labels": {
				"app": "nginx",
				"env": "qa"
			}
		},
		"spec": {
			"replicas":1,
			"selector": {
				"matchLabels": {
					"app": "nginx"
				}
			},
			"template": {
				"metadata": {
					"labels": {
						"app": "nginx",
						"env": "qa"
					}
				},
				"spec" : {
					"containers": [
						{
							"name":"test-pod2",
							"image": "nginx:alpine",
							"ports": [
								{
									"containerPort": 80
								}
							]
						}
					]
				}
			}
		}
	}
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "deployment", consumes = "application/json")
	@ResponseBody
	public RespObject deployment(@RequestBody V1beta2Deployment body) {
		AppsV1beta2Api api = new AppsV1beta2Api();
		try {

			V1beta2Deployment dep = api.createNamespacedDeployment(
			        body.getMetadata().getNamespace() == null ? "default" : body.getMetadata().getNamespace(), body,
			        Boolean.FALSE.toString());

			return new RespObject(dep);
		} catch (ApiException e) {
			e.printStackTrace();
			return new RespObject(e.getMessage(), 1);
		}
	}
	
	/**
	 * 删除部署
		 {
			"kind": "Deployment",
			"metadata": {
				"namespace": "default",
				"name":"test-dep",
				"labels": {
					"app": "nginx",
					"env": "qa"
				}
			}
		}
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "deployment", consumes = "application/json")
	@ResponseBody
	public RespObject deleteDeployment(@RequestBody V1beta2Deployment body) {
		AppsV1beta2Api api = new AppsV1beta2Api();
		try {
			V1Status resp = api.deleteNamespacedDeployment(body.getMetadata().getName(),
			        body.getMetadata().getNamespace(), options, pretty, 10, false, "Foreground");
			return new RespObject(resp);
		} catch (ApiException e) {
			e.printStackTrace();
			return new RespObject(e.getMessage(), 1);
		} catch (JsonSyntaxException e) {
			return new RespObject("success", 0);
		}
	}

	/**
	 * 创建
	 {
		"kind": "Namespace",
		"metadata": {
			"name":"test"
		}
	}
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "namespace", consumes = "application/json")
	@ResponseBody
	public RespObject namespace(@RequestBody V1Namespace body) {
		try {
			CoreV1Api api = new CoreV1Api();
			V1Namespace resp = api.createNamespace(body, pretty);
			return new RespObject(resp.getStatus());
		} catch (ApiException e) {
			e.printStackTrace();
			return new RespObject(e.getMessage(), 1);
		}
	}
	
	/**
	 * 删除Namespace
	 {
		"kind": "Namespace",
		"metadata": {
			"name":"test"
		}
	}
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, path = "namespace", consumes = "application/json")
	@ResponseBody
	public RespObject deleteNamespace(@RequestBody V1Namespace body) {
		try {
			CoreV1Api api = new CoreV1Api();
			V1Status resp = api.deleteNamespace(body.getMetadata().getName(), options, pretty, 10, false, "");;
			return new RespObject(resp.getStatus());
		} catch (ApiException e) {
			e.printStackTrace();
			return new RespObject(e.getMessage(), 1);
		} catch (JsonSyntaxException e) {
			return new RespObject("success", 0);
		}
	}
	
	/**
	 {
	  "kind": "PersistentVolume",
	  "metadata": {
	    "name": "test-pc",
	    "namespace": "default"
	  },
	  "spec": {
	    "capacity": {
	      "storage": "2Gi"
	    },
	    "accessModes": [
	      "ReadWriteMany"
	    ],
	    "persistentVolumeReclaimPolicy": "Retain",
	    "storageClassName": "slow",
	    "mountOptions": [
	      "hard",
	      "nfsvers=4.1"
	    ],
	    "nfs": {
	      "path": "/test",
	      "server": "192.168.166.147"
	    }
	  }
	}
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "persistentVolume", consumes = "application/json")
	@ResponseBody
	public RespObject createPersistentVolume(@RequestBody V1PersistentVolume body) {
		try {
			CoreV1Api api = new CoreV1Api();
			V1PersistentVolume resp = api.createPersistentVolume(body, pretty);
			return new RespObject(resp.getStatus());
		} catch (ApiException e) {
			e.printStackTrace();
			return new RespObject(e.getMessage(), 1);
		}
	}
	
	/**
	 {
	  "kind": "PersistentVolumeClaim",
	  "apiVersion": "v1",
	  "metadata": {
	    "name": "test-pvc"
	  },
	  "spec": {
	    "accessModes": [
	      "ReadWriteMany"
	    ],
	    "resources": {
	      "requests": {
	        "storage": "2Gi"
	      }
	    },
	    "storageClassName": "slow"
	  }
	}
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, path = "persistentVolumeClaim", consumes = "application/json")
	@ResponseBody
	public RespObject createPersistentVolumeClaim(@RequestBody V1PersistentVolumeClaim body) {
		try {
			CoreV1Api api = new CoreV1Api();
			V1PersistentVolumeClaim resp = api.createNamespacedPersistentVolumeClaim(
			        body.getMetadata().getNamespace() == null ? "default" : body.getMetadata().getNamespace(), body,
			        pretty);
			return new RespObject(resp.getStatus());
		} catch (ApiException e) {
			e.printStackTrace();
			return new RespObject(e.getMessage(), 1);
		}
	}
}
