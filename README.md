# multithreaded-server
Multithreaded **JSON database** with API enabling CRUD operations

project was completed as a graduate project for Java Developer track on Jet Brains Academy 


The key should not only be a string since the user needs to retrieve part of the JSON value. For example, in the code snippet below, the user wants to get only the surname of the person:

{
... ,

    "person": {
        "name": "Adam",
        "surname": "Smith"
    }
    ...
}
Then, the user should type the full path to this field in a form of a JSON array: ["person", "surname"]. If the user wants to get the full person object, then they should type ["person"]. The user should be able to set separate values inside JSON values. For example, it should be possible to set only the surname using a key ["person", "surname"] and any value including another JSON. Moreover, the user should be able to set new values inside other JSON values. For example, using a key ["person", "age"] and a value 25, the person object should look like this:

{
... ,

    "person": {
        "name": "Adam",
        "surname": "Smith",
        "age": 25

    }
    ...
}
If there are no root objects, the server should create them, too. For example, if the database does not have a "person1" key but the user set the value {"id1": 12, "id2": 14} for the key ["person1", "inside1", "inside2"], then the database will have the following structure:
{
... ,
"person1": {
"inside1": {
"inside2" : {
"id1": 12,
"id2": 14
}
}
},
...
}
The deletion of objects should follow the same rules. If a user deletes the object above by the key ["person1", "inside1", "inside2], then only "inside2" should be deleted, not "inside1" or "person1". See the example below:

{
... ,
"person1": {
"inside1": { }
}

    ...
}



