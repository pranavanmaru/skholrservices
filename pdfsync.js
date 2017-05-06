'use strict';

console.log('Loading function');

const mysql = require('mysql');
const aws = require('aws-sdk');

const s3 = new aws.S3({ apiVersion: '2006-03-01' });

var connection = mysql.createConnection({
			  host     : 'skholrrds.csoycoxzssog.us-east-1.rds.amazonaws.com',
			  user     : 'skholrsrao7',
			  password : 'sherlockH55',
			  database : 'skholrdocRDS'
			});

exports.handler = (event, context, callback) => {
    //console.log('Received event:', JSON.stringify(event, null, 2));

    // Get the object from the event and show its content type
    const bucket = event.Records[0].s3.bucket.name;
    const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));
    const params = {
        Bucket: bucket,
        Key: key,
    };
    s3.getObject(params, (err, data) => {
        if (err) {
            console.log(err);
            const message = `Error getting object ${key} from bucket ${bucket}. Make sure they exist and your bucket is in the same region as this function.`;
            console.log(message);
            callback(message);
        } else {
            console.log('CONTENT TYPE:', data.ContentType);			
			console.log('Key: ' + params.Key + ' Date: ' + mysqlDate());	
			connection.connect();
            var query = 'insert into DocsRepo(FileName,Processed,CreatedDate) values("' + params.Key +'", 0, "'+ mysqlDate() +'")';
			console.log(query);
			connection.query(query, function (error, results, fields) {
			  if (error) throw error;
			});

			connection.end();
            callback(null, "success");
        }
    });
    
    
};

function mysqlDate(){
    var date = new Date();
    return date.toISOString().split('T')[0];
}
