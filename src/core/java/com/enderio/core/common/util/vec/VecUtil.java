package com.enderio.core.common.util.vec;

import com.mojang.datafixers.util.Pair;

import java.awt.*;
import java.util.Optional;

public class VecUtil {

    /**
     * Returns the distance from a point to a plane.
     *
     * @param plane the plane.
     * @param point the point.
     * @return the distance between them.
     */
    public static double distanceFromPointToPlane(Vector4d plane, EnderVector3d point) {
        Vector4d newPoint = new Vector4d(point.x(), point.y(), point.z(), 1);
        return plane.dot(newPoint);
    }

    public static Vector4d computePlaneEquation(Vector4d a, Vector4d b, Vector4d c) {
        return computePlaneEquation(new EnderVector3d(a.x(), a.y(), a.z()), new EnderVector3d(b.x(), b.y(), b.z()), new EnderVector3d(c.x(), c.y(), c.z()));
    }

    public static EnderVector3d clamp(EnderVector3d v, double min, double max) {
        double vx = clamp(v.x(), min, max);
        double vy = clamp(v.y(), min, max);
        double vz = clamp(v.z(), min, max);
        return new EnderVector3d(vx, vy, vz);
    }

    public static double clamp(double val, double min, double max) {
        return val < min ? min : Math.min(val, max);
    }

    public static int clamp(int val, int min, int max) {
        return val < min ? min : Math.min(val, max);
    }

    /**
     * Compute the plane equation <code>Ax + By + Cz + D = 0</code> for the plane
     * defined by the three points which lie on the plane, a, b, and c, and
     * placing the result into r. The plane equation can be summarised as the
     * normal vector of the plane (A,B,C) and the distance to the plane from the
     * origin (D).
     *
     * @param a vector a.
     * @param b vector b.
     * @param c vector c.
     * @return the result (A,B,C,D) of plane equation.
     */
    public static Vector4d computePlaneEquation(EnderVector3d a, EnderVector3d b, EnderVector3d c) {
        // compute normal vector
        double ix = c.x() - a.x();
        double iy = c.y() - a.y();
        double iz = c.z() - a.z();
        EnderVector3d i = new EnderVector3d(ix, iy, iz);

        double jx = b.x() - a.x();
        double jy = b.y() - a.y();
        double jz = b.z() - a.z();
        EnderVector3d j = new EnderVector3d(jx, jy, jz);

        EnderVector3d k = j.copy().cross(i).normalize();

        // plane equation: Ax + By + Cz + D = 0
        double w = -(k.x() * a.x() + k.y() * a.y() + k.z() * a.z()); // D
        return new Vector4d(k.x(), k.y(), k.z(), w);
    }

    /**
     * Projects the point onto the plane.
     *
     * @param plane the plane.
     * @param point the point.
     */
    public static Vector4d projectPointOntoPlane(Vector4d plane, Vector4d point) {
        double distance = plane.dot(point);
        EnderVector3d planeNormal = new EnderVector3d(plane.x(), plane.y(), plane.z()).normalize().scale(distance);
        return point.copy().sub(new Vector4d(planeNormal.x(), planeNormal.y(), planeNormal.z(), 0));
    }

    /**
     * This method calculates the intersection between a line and a plane.
     *
     * @param plane         the plane (x,y,z = normal, w = distance from origin)
     * @param pointInLine   a point in the line.
     * @param lineDirection the direction of the line.
     * @return the intersection or null if there is no intersection or the line is
     * on the plane.
     */
    public static Optional<EnderVector3d> computeIntersectionBetweenPlaneAndLine(Vector4d plane, EnderVector3d pointInLine, EnderVector3d lineDirection) {
        // check for no intersection
        EnderVector3d planeNormal = new EnderVector3d(plane.x(), plane.y(), plane.z());
        if (planeNormal.dot(lineDirection) == 0) {
            // line and plane are perpendicular
            return Optional.empty();
        }
        // check if line is on the plane
        if (planeNormal.dot(pointInLine) + plane.w() == 0) {
            return Optional.empty();
        }

        // we have an intersection
        Vector4d point = new Vector4d(pointInLine.x(), pointInLine.y(), pointInLine.z(), 1);
        Vector4d lineNorm = new Vector4d(lineDirection.x(), lineDirection.y(), lineDirection.z(), 0);
        double t = -(plane.dot(point) / plane.dot(lineNorm));

        EnderVector3d result = pointInLine.copy();
        return Optional.of(result.add(lineDirection.scale(t)));
    }

    /**
     * This function computes the ray that goes from the eye, through the
     * specified pixel.
     *
     * @param x the x pixel location (x = 0 is the left most pixel)
     * @param y the y pixel location (y = 0 is the bottom most pixel)
     * @return A pair of the eye and normal description the directional component of the ray.
     */
    public static Pair<EnderVector3d, EnderVector3d> computeRayForPixel(Rectangle vp, Matrix4d ipm, Matrix4d ivm, int x, int y) {
        // grab the eye's position
        EnderVector3d eyeOut = ivm.getTranslation();

        Matrix4d vpm = new Matrix4d();
        vpm.mul(ivm, ipm);

        // Calculate the pixel location in screen clip space (width and height from
        // -1 to 1)
        double screenX = (((x - vp.getX()) / vp.getWidth()) * 2.0) - 1.0;
        double screenY = (((y - vp.getY()) / vp.getHeight()) * 2.0) - 1.0;

        // Now calculate the XYZ location of this point on the near plane
        Vector4d nearPlane = new Vector4d(screenX, screenY, -1, 1.);
        vpm.transform(nearPlane);

        EnderVector3d nearXYZ = new EnderVector3d(nearPlane.x() / nearPlane.w(), nearPlane.y() / nearPlane.w(), nearPlane.z() / nearPlane.w());

        // and then on the far plane
        Vector4d farPlane = nearPlane.withZ(1);
        vpm.transform(farPlane);

        EnderVector3d farXYZ = new EnderVector3d(farPlane.x() / farPlane.w(), nearPlane.y() / farPlane.w(), nearPlane.z() / farPlane.w());
        return Pair.of(eyeOut, farXYZ.copy().sub(nearXYZ).normalize());
    }

    /**
     * Creates a perspective projection matrix.
     *
     * @param fovDegrees     The field of view angle in degrees.
     * @param near           near plane.
     * @param far            far plane.
     * @param viewportWidth  viewport width.
     * @param viewportHeight viewport height.
     * @return the matrix.
     */

    public static Matrix4d createProjectionMatrixAsPerspective(double fovDegrees, double near, double far, int viewportWidth, int viewportHeight) {
        Matrix4d matrix = new Matrix4d();
        // for impl details see gluPerspective doco in OpenGL reference manual
        double aspect = (double) viewportWidth / (double) viewportHeight;

        double theta = Math.toRadians(fovDegrees) / 2d;
        double f = Math.cos(theta) / Math.sin(theta);

        double a = (far + near) / (near - far);
        double b = (2d * far * near) / (near - far);

        matrix.set(new double[] { f / aspect, 0, 0, 0, 0, f, 0, 0, 0, 0, a, b, 0, 0, -1, 0 });

        return matrix;
    }

    /**
     * Creates a projection matrix as per glFrustum.
     *
     * @param left   coordinate of left clip plane.
     * @param right  coordinate of right clip plane.
     * @param bottom coordinate of bottom clip plane.
     * @param top    coordinate of left top plane.
     * @param zNear  distance of the near plane.
     * @param zFar   distance of the near plane.
     * @return the matrix.
     */
    public static Matrix4d createProjectionMatrixAsPerspective(double left, double right, double bottom, double top, double zNear, double zFar) {
        double A = (right + left) / (right - left);
        double B = (top + bottom) / (top - bottom);
        double C = (Math.abs(zFar) > Double.MAX_VALUE) ? -1. : -(zFar + zNear) / (zFar - zNear);
        double D = (Math.abs(zFar) > Double.MAX_VALUE) ? -2. * zNear : -2.0 * zFar * zNear / (zFar - zNear);

        Matrix4d matrix = new Matrix4d();
        matrix.set(new double[] { 2.0 * zNear / (right - left), 0.0, 0.0, 0.0, 0.0, 2.0 * zNear / (top - bottom), 0.0, 0.0, A, B, C, -1.0, 0.0, 0.0, D, 0.0 });

        matrix.transpose();
        return matrix;
    }

    /**
     * Sets the orthographic projection matrix.
     *
     * @param left   the left value.
     * @param right  the right value.
     * @param bottom the bottom value.
     * @param top    the top value.
     * @param near   near plane.
     * @param far    far plane.
     * @return the ortho matrix.
     */

    public static Matrix4d createProjectionMatrixAsOrtho(double left, double right, double bottom, double top, double near, double far) {
        Matrix4d matrix = new Matrix4d();
        // for impl details see glOrtho doco in OpenGL reference manual
        double tx = -((right + left) / (right - left));
        double ty = -((top + bottom) / (top - bottom));
        double tz = -((far + near) / (far - near));

        matrix.set(new double[] { 2d / (right - left), 0, 0, tx, 0, 2d / (top - bottom), 0, ty, 0, 0, -2d / (far - near), tz, 0, 0, 0, 1 });

        return matrix;
    }

    /**
     * Sets the near and far values on an existing perspective projection matrix.
     *
     * @param projMat the matrix to be modified.
     * @param near    the new near value.
     * @param far     the new far value.
     */
    public static void setNearFarOnPerspectiveProjectionMatrix(Matrix4d projMat, double near, double far) {
        projMat.transpose();

        double transNearPlane = (-near * projMat.getElement(2, 2) + projMat.getElement(3, 2)) / (-near * projMat.getElement(2, 3) + projMat.getElement(3, 3));
        double transFarPlane = (-far * projMat.getElement(2, 2) + projMat.getElement(3, 2)) / (-far * projMat.getElement(2, 3) + projMat.getElement(3, 3));

        double ratio = Math.abs(2.0 / (transNearPlane - transFarPlane));
        double center = -(transNearPlane + transFarPlane) / 2.0;

        Matrix4d mat = new Matrix4d();
        mat.setIdentity();
        mat.setElement(2, 2, ratio);
        mat.setElement(3, 2, center * ratio);

        projMat.mul(mat);
        projMat.transpose();
    }

    /**
     * Creates a look at matrix.
     *
     * @param eyePos    the position of the eye.
     * @param lookAtPos the point to look at.
     * @param upVec     the up vector.
     * @return the look at matrix.
     */
    public static Matrix4d createMatrixAsLookAt(EnderVector3d eyePos, EnderVector3d lookAtPos, EnderVector3d upVec) {
        EnderVector3d eye = eyePos.copy();
        EnderVector3d up = upVec.copy();
        EnderVector3d forwardVec = lookAtPos.copy().sub(eye).normalize();

        EnderVector3d sideVec = forwardVec.cross(up).normalize();
        EnderVector3d upVed = sideVec.cross(forwardVec).normalize();

        Matrix4d mat = new Matrix4d(sideVec.x(), sideVec.y(), sideVec.z(), 0, upVed.x(), upVed.y(), upVed.z(), 0, -forwardVec.x(), -forwardVec.y(),
            -forwardVec.z(), 0, 0, 0, 0, 1);

        eye.negate();
        return mat.transformNormal(eye).setTranslation(eye);
    }

    /**
     * Pre-multiplies the vector by the matrix.
     *
     * @param v   the vector.
     * @param mat the matrix.
     * @return the result of the multiplication.
     */
    public static EnderVector3d preMultiply(EnderVector3d v, Matrix4d mat) {
        Matrix4d m = new Matrix4d(mat);
        m.transpose();
        double d = 1.0f / (m.getElement(0, 3) * v.x() + m.getElement(1, 3) * v.y() + m.getElement(2, 3) * v.z() + m.getElement(3, 3));
        double x = (m.getElement(0, 0) * v.x() + m.getElement(1, 0) * v.y() + m.getElement(2, 0) * v.z() + m.getElement(3, 0)) * d;
        double y = (m.getElement(0, 1) * v.x() + m.getElement(1, 1) * v.y() + m.getElement(2, 1) * v.z() + m.getElement(3, 1)) * d;
        double z = (m.getElement(0, 2) * v.x() + m.getElement(1, 2) * v.y() + m.getElement(2, 2) * v.z() + m.getElement(3, 2) * d);
        return v.set(x, y, z);
    }

    /**
     * Extracts the s,t,r and q eye planes from the specified matrix.
     *
     * @param matrix the matrix to extract the planes from.
     * @return the s,t,r and q planes from the specified matrix.
     */
    public static Vector4d[] getEyePlanesForMatrix(Matrix4d matrix) {
        Matrix4d copy = new Matrix4d(matrix);
        copy.transpose();

        Vector4d[] res = new Vector4d[4];
        // s plane
        res[0] = new Vector4d(copy.getElement(0, 0), copy.getElement(1, 0), copy.getElement(2, 0), copy.getElement(3, 0));
        // t plane
        res[1] = new Vector4d(copy.getElement(0, 1), copy.getElement(1, 1), copy.getElement(2, 1), copy.getElement(3, 1));
        // r plane
        res[2] = new Vector4d(copy.getElement(0, 2), copy.getElement(1, 2), copy.getElement(2, 2), copy.getElement(3, 2));
        // q plane
        res[3] = new Vector4d(copy.getElement(0, 3), copy.getElement(1, 3), copy.getElement(2, 3), copy.getElement(3, 3));

        return res;
    }

    /**
     * Extracts the up vec from the given view matrix
     *
     * @param matrix the view matrix
     * @return the up vec
     */
    public static EnderVector3d getUpVecFromMatrix(Matrix4d matrix) {
        return new EnderVector3d(matrix.getElement(1, 0), matrix.getElement(1, 1), matrix.getElement(1, 2)).normalize();
    }

    /**
     * Extracts the side vec from the given view matrix
     *
     * @param matrix the view matrix
     * @return the side vec
     */
    public static EnderVector3d getSideVecFromMatrix(Matrix4d matrix) {
        return new EnderVector3d(matrix.getElement(0, 0), matrix.getElement(0, 1), matrix.getElement(0, 2)).normalize();
    }

    /**
     * Extracts the look vec from the given view matrix
     *
     * @param matrix the view matrix
     * @return the look vec
     */
    public static EnderVector3d getLookVecFromMatrix(Matrix4d matrix) {
        return new EnderVector3d(matrix.getElement(2, 0), matrix.getElement(2, 1), matrix.getElement(2, 2)).negate().normalize();
    }
}
