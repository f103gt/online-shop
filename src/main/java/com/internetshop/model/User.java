    package com.internetshop.model;

    public class User {

        private int id;
        private String username;
        private String password;
        private Role role;

        private String address;
        private String email;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public User(int id,
                    String username,
                    String password,
                    Role role,
                    String address,
                    String email) {
            this.id = id;
            this.username = username;
            this.password = password;
            this.role = role;
            this.email = email;
            this.address = address;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getAddress() {
            return address;
        }

        public String getEmail() {
            return email;
        }
    }
