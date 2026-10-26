import { useLocation, useNavigate } from "react-router-dom";
import { AuthForm } from "../components/AuthForm";
import { AuthStory } from "../components/AuthStory";

interface AuthPageProps {
  initialSignup: boolean;
  onSuccess: () => void;
}

export function AuthPage({ initialSignup, onSuccess }: AuthPageProps) {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <div className="auth-layout">
      <AuthStory />
      <AuthForm
        key={initialSignup ? "register" : "login"}
        initialSignup={initialSignup}
        onSuccess={onSuccess}
        onModeChange={(signup) =>
          navigate(`${signup ? "/register" : "/login"}${location.search}`)
        }
      />
    </div>
  );
}
