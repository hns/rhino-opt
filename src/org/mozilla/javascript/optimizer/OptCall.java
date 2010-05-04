package org.mozilla.javascript.optimizer;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public abstract class OptCall extends ScriptableObject {

    public Object arguments;

    public OptCall(Scriptable parentScope, Object[] args) {
        setParentScope(parentScope);
        arguments = new OptArguments(args);
    }

    protected abstract Object getArgument(int index);
    protected abstract void putArgument(int index, Object value);

    @Override
    public String getClassName() {
        return "Call";
    }

    class OptArguments extends ScriptableObject {

        Object[] args;
        int length;

        OptArguments(Object[] args) {
            this.args = args;
            this.length = args.length;
        }

        public String getClassName() {
            return "Arguments";
        }

        @Override
        public Object get(String name, Scriptable start) {
            if ("length".equals(name)) {
                return Integer.valueOf(length);
            } else if ("constructor".equals(name)) {
                return ScriptableObject.getTopScopeValue(this, "Object");
            }
            return super.get(name, start);
        }

        @Override
        public Object get(int index, Scriptable start) {
            if (index < 0 || index >= length) {
                return super.get(index, start);
            }
            return getArgument(index);
        }

        @Override
        public void put(int index, Scriptable start, Object value) {
            if (index < 0 || index >= length) {
                super.put(index, start, value);
            }
            putArgument(index, value);
        }

        @Override
        public boolean has(int index, Scriptable start) {
            return super.has(index, start) || (index >= 0 && index < length);
        }

        @Override
        public Object[] getIds() {
            Object[] ids = new Object[length];
            for (int i = 0; i < length; i++) {
                ids[i] = Integer.valueOf(i);
            }
            return ids;
        }
    }

}

