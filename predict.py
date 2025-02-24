# import sys
# import os
# import pymysql
# import pandas as pd
# from sklearn.linear_model import LinearRegression
# from sklearn.model_selection import train_test_split
# import joblib

# def train_and_save_model(account_id):
#     connection = pymysql.connect(
#         host='localhost',
#         user='root',
#         password='pratyush@10',
#         db='expenses'
#     )

#     query = "SELECT DISTINCT category FROM expenditure WHERE account_id = %s"
#     categories_df = pd.read_sql(query, connection, params=(account_id,))
#     categories = categories_df['category'].tolist()

#     if not categories:
#         print("No categories found for this account.")
#         connection.close()
#         return

#     query = "SELECT * FROM expenditure WHERE account_id = %s"
#     df = pd.read_sql(query, connection, params=(account_id,))

#     connection.close()

#     data = pd.get_dummies(df, columns=['category'], drop_first=True)
#     data.drop(columns=['id', 'account_id', 'date'], inplace=True)

#     X = data.drop(columns=['amount'])
#     y = data['amount']

#     X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)


#     model = LinearRegression()
#     model.fit(X_train, y_train)

#     # Save the trained model to a file
#     model_filename = f"model_account_{account_id}.joblib"
#     joblib.dump(model, model_filename)

#     print(f"Model for account {account_id} saved to {model_filename}")

# def predict_expense(account_id, selected_category):
#     connection = pymysql.connect(
#         host='localhost',
#         user='root',
#         password='pratyush@10',
#         db='expenses'
#     )

#     # Load the trained model
#     model_filename = f"model_account_{account_id}.joblib"
#     model = joblib.load(model_filename)

#     # Prepare input data for prediction
#     input_data = pd.DataFrame({'category_' + selected_category: [1]})

#     # Make the prediction
#     predicted_amount = model.predict(input_data)

#     print(f"Predicted expense for category '{selected_category}': ${predicted_amount[0]:.2f}")

# if __name__ == "__main":
#     if len(sys.argv) < 2:
#         print("Usage: predict.py <account_id> [category]")
#         sys.exit(1)

#     user_account_id = int(sys.argv[1])

#     if len(sys.argv) == 3:
#         selected_category = sys.argv[2]
#         predict_expense(user_account_id, selected_category)
#     else:
#         train_and_save_model(user_account_id)



import sys
import os
import pymysql
import pandas as pd
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import train_test_split
import joblib

def train_and_save_model(account_id):
    connection = pymysql.connect(
        host='localhost',
        user='root',
        password='pratyush@10',
        db='expenses'
    )

    query = "SELECT DISTINCT category FROM expenditure WHERE account_id = %s"
    categories_df = pd.read_sql(query, connection, params=(account_id,))
    categories = categories_df['category'].tolist()

    if not categories:
        print("No categories found for this account.")
        connection.close()
        return

    for category in categories:
        query = "SELECT * FROM expenditure WHERE account_id = %s AND category = %s"
        df = pd.read_sql(query, connection, params=(account_id, category))

        data = pd.get_dummies(df, columns=['category'], drop_first=True)
        data.drop(columns=['id', 'account_id', 'date'], inplace=True)

        X = data.drop(columns=['amount'])
        y = data['amount']

        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

        model = LinearRegression()
        model.fit(X_train, y_train)

        # Save the trained model for this category to a file
        model_filename = f"model_account_{account_id}_category_{category}.joblib"
        joblib.dump(model, model_filename)

        print(f"Model for account {account_id}, category {category} saved to {model_filename}")

    connection.close()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: predict.py <account_id>")
        sys.exit(1)

    user_account_id = int(sys.argv[1])

    # Train and save models for all categories of the specified account
    train_and_save_model(user_account_id)