// import { defaultInstance } from '../api/api';

// import { useEffect } from 'react';

export interface Login {
  restApiKey: String;
  redirectUri: String;
  link: String;
}

const LoginPage = () => {
  //   const axiosInstance = defaultInstance();

  //   const redirectUrl =

  const redirectUrl = 'http://localhost:3000/login-redirect';

  const link = `https://kauth.kakao.com/oauth/authorize?client_id=bab90d2b24304bb1f5b4c07938ff0fcc&redirect_uri=${redirectUrl}&response_type=code`;

  const loginHandler = () => {
    window.location.href = link;
  };

  return (
    <div>
      <button onClick={loginHandler}>로그인</button>
    </div>
  );
};

export default LoginPage;