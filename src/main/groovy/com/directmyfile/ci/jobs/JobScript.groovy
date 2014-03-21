package com.directmyfile.ci.jobs

abstract class JobScript extends Script {
	String name
	List<Map<String, Object>> tasks = []
	List<String> artifacts = []
	Map<String, Object> notify = [:]
	Map<String, Object> scm = [:]
	List<String> requirements = []
}