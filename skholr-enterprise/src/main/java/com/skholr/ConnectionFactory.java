package com.skholr;
/*
 * Connection Factory provides outer layer with connection instances for:
 * 1) S3 instance related to CloudFront
 * 2) ElasticSearch instance to store parsed content back
 * TODO: 
 * 1) Return EMR Connection Object to DataBroker
 * 2) Return RDS Connection Object to DataBroker
 */

import java.sql.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.s3.AmazonS3Client;

public class ConnectionFactory {
	private static AWSCredentials credentials = new BasicAWSCredentials("AKIAJ4O4LHGNACP54JAQ",
			"eWce89fjnwL1okOrEhC9txjQSZ8fCWwCwbPOmvHo");
	private static AmazonS3Client s3Client = null;
	private static AmazonElasticMapReduceClient emrClient = null;
	private static Connection RDSConnection = null;

	private static final String AWS_EMR_HOST = "jdbc:hive//ec2-54-174-239-184.compute-1.amazonaws.com:10000";

	private static final String RDS_HOSTNAME = "skholrrds.csoycoxzssog.us-east-1.rds.amazonaws.com";
	private static final String RDS_PORT = "3306";
	private static final String RDS_DB_INSTANCE = "skholrrds";
	private static final String RDS_USERNAME = "skholrsrao7";
	private static final String RDS_PASSWORD = "sherlockH55";
	private static final String RDS_DB_NAME = "skholrdocRDS";

	private ConnectionFactory() {
	}

	@SuppressWarnings("deprecation")
	public static AmazonS3Client getS3Client() {

		if (s3Client == null) {

			s3Client = new AmazonS3Client(credentials);
		}

		return s3Client;
	}

	public static Connection getRDSConnection() throws ClassNotFoundException, SQLException {

		if (RDSConnection == null || RDSConnection.isClosed()) {

			Class.forName("com.mysql.jdbc.Driver");
			String jdbcUrl = "jdbc:mysql://" + RDS_HOSTNAME + ":" + RDS_PORT + "/" + RDS_DB_NAME + "?user=" + RDS_USERNAME
					+ "&password=" + RDS_PASSWORD;
			RDSConnection = DriverManager.getConnection(jdbcUrl);

		}
		return RDSConnection;
	}

	public static AmazonElasticMapReduceClient getEMRClient() {

		if (emrClient == null) {

			emrClient = new AmazonElasticMapReduceClient(credentials);
		}
		return emrClient;
	}
}
