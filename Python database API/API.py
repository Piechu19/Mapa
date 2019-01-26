from flask import Flask, request
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from json import dumps
from flask import jsonify

db_connect = create_engine('oracle+cx_oracle://system:oracle@192.168.56.2:1521/xe')
app = Flask(__name__)
api = Api(app)

class Employees(Resource):
    def get(self):
        conn = db_connect.connect() # connect to database
        query = conn.execute("select * from Users") # This line performs query and returns json result
        return {'Users': [i[1] for i in query.cursor.fetchall()]} # Fetches first column that is Employee ID

class Tracks(Resource):
    def get(self):
        conn = db_connect.connect()
        query = conn.execute("select name, password from users")
        result = {'data': [dict(zip(tuple (query.keys()) ,i)) for i in query.cursor]}     
        return jsonify(result)

class Check_Password(Resource):
    def get(self, password):
        conn = db_connect.connect()
        query = conn.execute("select name from users where password ='%s'" %(password))
        result = {'data': [dict(zip(tuple (query.keys()) ,i)) for i in query.cursor]}     
        return jsonify(result)

class Check_Name(Resource):
    def get(self, name):
        conn = db_connect.connect()
        query = conn.execute("select name, password from users where name ='%s'" %(name))
        result = {'data': [dict(zip(tuple (query.keys()) ,i)) for i in query.cursor]}     
        return jsonify(result)

class Check_List(Resource):
    def get(self):
        conn = db_connect.connect()
        query = conn.execute("select product from list")
        result = {'list': [dict(zip(tuple (query.keys()) ,i)) for i in query.cursor]}     
        return jsonify(result)

class Add_List(Resource):
    def get(self ,prod_name):
        conn = db_connect.connect()
        query = conn.execute("INSERT INTO List VALUES('%s')" %(prod_name))

class Cut_List(Resource):
    def get(self,prod_id):
        conn = db_connect.connect()
        #query = conn.execute("DELETE FROM List  WHERE id = %s" %(prod_id))
        query = conn.execute("DELETE FROM List  WHERE product = '%s'" %(prod_id))    


api.add_resource(Employees, '/employees') # Route_1
api.add_resource(Check_Password, '/password/<password>') # Route_2
api.add_resource(Check_Name, '/password/<name>') # Route_3
api.add_resource(Check_List, '/list') # Route_3
api.add_resource(Add_List, '/list/<prod_name>') # Route_3
api.add_resource(Cut_List, '/list_cut/<prod_id>') # Route_3


if __name__ == '__main__':
     app.run(host='10.7.71.228',port='5002')
